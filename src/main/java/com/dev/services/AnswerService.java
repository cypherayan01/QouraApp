package com.dev.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dev.adapter.AnswerAdapter;
import com.dev.dto.AnswerRequestDTO;
import com.dev.dto.AnswerResponseDTO;
import com.dev.dto.QuestionResponseDTO;
import com.dev.events.AnswerNotificationEvent;
import com.dev.models.Answer;
import com.dev.models.User;
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
    private final IUserService userService;
    private final IQuestionService questionService;

    @Override
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO,String userId) {
        Answer answer = Answer.builder()
          .userId(userId)  // Set userId
          .content(answerRequestDTO.getContent())
          .questionId(answerRequestDTO.getQuestionId())
          .build();

      return answerRepository.save(answer)
          .flatMap(savedAnswer ->
              // Get user details and question title
              Mono.zip(
                  userService.getUserById(userId),
                  questionService.getQuestionById(savedAnswer.getQuestionId())
              )
              .map(tuple -> {
                  User user = tuple.getT1();
                  QuestionResponseDTO question = tuple.getT2();

                  return AnswerResponseDTO.builder()
                      .id(savedAnswer.getId())
                      .content(savedAnswer.getContent())
                      .questionId(savedAnswer.getQuestionId())
                      .createdAt(savedAnswer.getCreatedAt())
                      .updatedAt(savedAnswer.getUpdatedAt())
                      // User details
                      .userId(user.getId())
                      .username(user.getUsername())
                      .displayName(user.getDisplayName())
                      .profilePictureUrl(user.getProfilePictureUrl())
                      .likeCount(0L)
                      // Question context
                      .questionTitle(question.getTitle())
                      .build();
              })
          );
    }

    @Override
    public Mono<AnswerResponseDTO> getAnswerById(String id) {
        return answerRepository.findById(id)
            .flatMap(answer -> 
                userService.getUserById(answer.getUserId())
                    .map(user -> AnswerAdapter.mapToResponseDTO(answer, user))
            );
    }

    @Override
    public Flux<AnswerResponseDTO> getAllAnswers() {
        return answerRepository.findAll()
            .flatMap(answer -> 
                userService.getUserById(answer.getUserId())
                    .map(user -> AnswerAdapter.mapToResponseDTO(answer, user))
            );
    }
    @Override
    public Flux<AnswerResponseDTO> getAnswersByQuestionId(String questionId) {
        return answerRepository.findByQuestionId(questionId)
            .flatMap(answer -> 
                Mono.zip(
                    userService.getUserById(answer.getUserId()),
                    questionService.getQuestionById(questionId)
                )
                .map(tuple -> {
                    User user = tuple.getT1();
                    QuestionResponseDTO question = tuple.getT2();
                    return AnswerAdapter.mapToResponseDTO(answer, user, question.getTitle());
                })
            );
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
            .flatMap(updatedAnswer ->
                userService.getUserById(updatedAnswer.getUserId())
                    .map(user -> AnswerAdapter.mapToResponseDTO(updatedAnswer, user))
            );
    }

    
    @Override
    public Mono<Void> deleteAnswer(String id) {
        return answerRepository.deleteById(id);
    }

    
}