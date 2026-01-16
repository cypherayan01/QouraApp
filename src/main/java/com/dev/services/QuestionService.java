package com.dev.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dev.adapter.QuestionAdapter;
import com.dev.dto.QuestionRequestDTO;
import com.dev.dto.QuestionResponseDTO;
import com.dev.events.ViewCountEvent;
import com.dev.models.Question;
import com.dev.producers.KafkaEventProducers;
import com.dev.repository.QuestionRepository;
import com.dev.utils.CursorUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    private final IUserService userService;

    private final KafkaEventProducers kafkaEventProducer;

    public QuestionService(QuestionRepository questionRepository,KafkaEventProducers kafkaEventProducer, IUserService userService) {
        this.questionRepository = questionRepository;
        this.kafkaEventProducer = kafkaEventProducer;
        this.userService = userService;
    }


    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO,String userId) {
        Question question = Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userId(userId)
                .build();
        Mono<Question> savedDTO= questionRepository.save(question);
        //return ans;
        return questionRepository.save(question)
          .flatMap(savedQuestion ->
              // Get user details and build response
              userService.getUserById(userId)
                  .map(user -> QuestionResponseDTO.builder()
                      .id(savedQuestion.getId())
                      .title(savedQuestion.getTitle())
                      .content(savedQuestion.getContent())
                      .views(savedQuestion.getViews())
                      .createdAt(savedQuestion.getCreatedAt())
                      .updatedAt(savedQuestion.getUpdatedAt())
                      // User details
                      .userId(user.getId())
                      .username(user.getUsername())
                      .displayName(user.getDisplayName())
                      .profilePictureUrl(user.getProfilePictureUrl())
                      .answerCount(0L) 
                      .likeCount(0L)   
                      .build())
          );
                
    }

    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm, int offset, int size) {
        return questionRepository.findByTitleOrContentContainingIgnoreCase(searchTerm, PageRequest.of(offset, size))
        .flatMap(question -> 
            userService.getUserById(question.getUserId())
                .map(user -> QuestionAdapter.toQuestionResponseDTO(question, user))
        )
        .doOnError(error -> System.out.println("Error searching questions: " + error))
        .doOnComplete(() -> System.out.println("Questions searched successfully"));
    }

    public Flux<QuestionResponseDTO> getAllQuestions(String cursor,int size){
        Pageable pageable = PageRequest.of(0,size);
        if(!CursorUtils.isValidCursor(cursor)){
            return questionRepository.findTop10ByOrderByCreatedAtAsc()
                    .take(size)
                    .flatMap(question -> 
                        userService.getUserById(question.getUserId())
                            .map(user -> QuestionAdapter.toQuestionResponseDTO(question, user))
                    )
                    .doOnError(error -> System.out.println("Error : "+error))
                    .doOnComplete(() -> System.out.println("Fetched successfully" + size));
        }else{
            LocalDateTime currentCursorTime = CursorUtils.parseCursor(cursor);
            return questionRepository.findByCreatedAtGreaterThanOrderByCreatedAtAsc(currentCursorTime,size)
                    .flatMap(question -> 
                        userService.getUserById(question.getUserId())
                            .map(user -> QuestionAdapter.toQuestionResponseDTO(question, user))
                    )
                    .doOnError(error -> System.out.println("Error : "+error))
                    .doOnComplete(() -> System.out.println("Fetched successfully"));
        }
    }

    @Override
    public Mono<QuestionResponseDTO> getQuestionById(String id) {
        return getQuestionByIdInternal(id, true);
    }
    
    public Mono<QuestionResponseDTO> getQuestionByIdInternal(String id, boolean shouldTrackView) {
        return questionRepository.findById(id)
                .flatMap(question -> 
                    userService.getUserById(question.getUserId())
                        .map(user -> QuestionAdapter.toQuestionResponseDTO(question, user))
                )
                .doOnError(error -> System.err.println("Error fetching question by ID: " + error.getMessage()))
                .doOnSuccess(response -> {
                    if (response != null) {
                        System.out.println("Question fetched successfully: " + response.getTitle());
                        
                        if (shouldTrackView) {
                            try {
                                ViewCountEvent viewCountEvent = new ViewCountEvent(id, "question", LocalDateTime.now());
                                kafkaEventProducer.publishViewCountEvent(viewCountEvent);
                                System.out.println("View count event published for question ID: " + id);
                            } catch (Exception e) {
                                System.err.println("Error publishing view count event: " + e.getMessage());
                            }
                        }
                    }
                });
    }
}