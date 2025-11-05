package com.dev.consumers;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dev.repository.QuestionRepository;
import com.dev.config.KafkaConfig;
import com.dev.events.ViewCountEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final QuestionRepository questionRepository;

    
    @KafkaListener(topics = KafkaConfig.TOPIC_NAME,
     groupId = "view-count-consumer", 
     containerFactory = "kafkaListenerContainerFactory")
    public void handleViewCount(ViewCountEvent viewCountEvent) {
    questionRepository.findById(viewCountEvent.getTargetId())
        .flatMap(question -> {
            System.out.println("Incrementing view count for question ID: " + question.getId());
            question.setViews(question.getViews()+1);
            return questionRepository.save(question);
        })
        .subscribe(
            updatedQuestion -> System.out.println("Updated view count for question ID: " + updatedQuestion.getId()),
            error -> System.err.println("Error updating view count: " + error.getMessage())
        );
    }
    
}
