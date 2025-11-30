package com.dev.consumers;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dev.config.KafkaConfig;
import com.dev.events.ViewCountEvent;
import com.dev.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final QuestionRepository questionRepository;

    
    @KafkaListener(topics = KafkaConfig.TOPIC_NAME,
     groupId = "view-count-consumer", 
     containerFactory = "kafkaListenerContainerFactory")
    public void handleViewCount(ViewCountEvent viewCountEvent) {
        try {
            System.out.println("Processing view count event for target ID: " + viewCountEvent.getTargetId());
            
            questionRepository.findById(viewCountEvent.getTargetId())
                .doOnNext(question -> {
                    System.out.println("Found question ID: " + question.getId() + ", current views: " + question.getViews());
                    question.setViews(question.getViews() == null ? 1 : question.getViews() + 1);
                })
                .flatMap(question -> {
                    System.out.println("Incrementing view count for question ID: " + question.getId());
                    return questionRepository.save(question);
                })
                .doOnSuccess(updatedQuestion -> 
                    System.out.println("Successfully updated view count for question ID: " + updatedQuestion.getId() + 
                                     " to " + updatedQuestion.getViews()))
                .doOnError(error -> 
                    System.err.println("Error updating view count for ID " + viewCountEvent.getTargetId() + 
                                     ": " + error.getMessage()))
                .onErrorResume(error -> {
                    System.err.println("Recovering from view count update error, continuing: " + error.getMessage());
                    return Mono.empty();
                })
                .subscribe(
                    success -> System.out.println("View count processing completed for ID: " + viewCountEvent.getTargetId()),
                    error -> System.err.println("Final error in view count processing: " + error.getMessage())
                );
                
        } catch (Exception e) {
            System.err.println("Critical error in handleViewCount: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
