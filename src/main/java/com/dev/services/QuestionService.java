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

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    private final KafkaEventProducers kafkaEventProducer;

    public QuestionService(QuestionRepository questionRepository,KafkaEventProducers kafkaEventProducer) {
        this.questionRepository = questionRepository;
        this.kafkaEventProducer = kafkaEventProducer;
    }


    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO) {
        Question question = Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mono<Question> savedDTO= questionRepository.save(question);
        return savedDTO.map(q -> QuestionResponseDTO.builder()
                .id(q.getId())
                .title(q.getTitle())
                .content(q.getContent())
                .createdAt(q.getCreatedAt())
                .build());
        //return ans;
                
    }

    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm, int offset, int size) {
        return questionRepository.findByTitleOrContentContainingIgnoreCase(searchTerm, PageRequest.of(offset, size))
        .map(QuestionAdapter::toQuestionResponseDTO)
        .doOnError(error -> System.out.println("Error searching questions: " + error))
        .doOnComplete(() -> System.out.println("Questions searched successfully"));
    }

    public Flux<QuestionResponseDTO> getAllQuestions(String cursor,int size){
        Pageable pageable = PageRequest.of(0,size);
        if(!CursorUtils.isValidCursor(cursor)){
            return questionRepository.findTop10ByOrderByCreatedAtAsc()
                    .take(size)
                    .map(QuestionAdapter::toQuestionResponseDTO)
                    .doOnError(error -> System.out.println("Error : "+error))
                    .doOnComplete(() -> System.out.println("Fetched successfully" + size));
        }else{
            LocalDateTime currentCursorTime = CursorUtils.parseCursor(cursor);
            return questionRepository.findByCreatedAtGreaterThanOrderByCreatedAtAsc(currentCursorTime,size)
                    .map(QuestionAdapter :: toQuestionResponseDTO)
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
                .map(QuestionAdapter::toQuestionResponseDTO)
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