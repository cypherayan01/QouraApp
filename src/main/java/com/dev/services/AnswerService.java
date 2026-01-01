package com.dev.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dev.adapter.AnswerAdapter;
import com.dev.dto.AnswerRequestDTO;
import com.dev.dto.AnswerResponseDTO;
import com.dev.events.AnswerNotificationEvent;
import com.dev.models.Answer;
import com.dev.producers.KafkaEventProducers;
import com.dev.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnswerService implements IAnswerService {

    private final AnswerRepository answerRepository;
    private final KafkaEventProducers kafkaEventProducers;

    @Override
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO) {
        Answer answer = Answer.builder()
            .content(answerRequestDTO.getContent())
            .questionId(answerRequestDTO.getQuestionId())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        return answerRepository.save(answer)
            .doOnSuccess(savedAnswer -> { 
                AnswerNotificationEvent notificationEvent = AnswerNotificationEvent.builder()
                    .answerId(savedAnswer.getId())
                    .questionId(savedAnswer.getQuestionId())
                    .answerContent(savedAnswer.getContent().length() > 50 ? 
                        savedAnswer.getContent().substring(0, 50) + "..." : savedAnswer.getContent())
                    .answererUserId("current_user") 
                    .questionOwnerUserId("question_owner")
                    .notificationType("NEW_ANSWER")
                    .timestamp(LocalDateTime.now())
                    .build();
                
                kafkaEventProducers.publishAnswerNotificationEvent(notificationEvent);
                System.out.println("Notification published automatically for new answer ID: " + savedAnswer.getId());
            })
            .map(AnswerAdapter::mapToResponseDTO);
    }

    @Override
    public Mono<AnswerResponseDTO> getAnswerById(String id) {
        return answerRepository.findById(id)
            .map(AnswerAdapter::mapToResponseDTO);
    }

    @Override
    public Flux<AnswerResponseDTO> getAllAnswers() {
        return answerRepository.findAll()
            .map(AnswerAdapter::mapToResponseDTO);
    }
    @Override
    public Flux<AnswerResponseDTO> getAnswersByQuestionId(String questionId) {
        return answerRepository.findByQuestionId(questionId)
            .map(AnswerAdapter::mapToResponseDTO);
    }

    @Override
    public Mono<AnswerResponseDTO> updateAnswer(String id, AnswerRequestDTO answerRequestDTO) {
        return answerRepository.findById(id)
            .flatMap(existingAnswer -> {
                existingAnswer.setContent(answerRequestDTO.getContent());
                return answerRepository.save(existingAnswer);
            })
            .doOnSuccess(updatedAnswer ->{
                System.out.println("Processing update for answer ID: " + updatedAnswer.getId()+"to send notification");
                AnswerNotificationEvent notificationEvent = AnswerNotificationEvent.builder()
                    .answerId(updatedAnswer.getId())
                    .questionId(updatedAnswer.getQuestionId())
                    .answerContent(updatedAnswer.getContent().length() > 50 ? 
                        updatedAnswer.getContent().substring(0, 50) + "..." : updatedAnswer.getContent())
                    .answererUserId("current_user")
                    .questionOwnerUserId("question_owner")
                    .notificationType("UPDATED_ANSWER")
                    .timestamp(LocalDateTime.now())
                    .build();
                    kafkaEventProducers.publishAnswerNotificationEvent(notificationEvent);
                    System.out.println("Notification published for updated answer ID: " + updatedAnswer.getId());
            })
            .map(AnswerAdapter::mapToResponseDTO);
    }

    
    @Override
    public Mono<Void> deleteAnswer(String id) {
        return answerRepository.deleteById(id);
    }

    
}