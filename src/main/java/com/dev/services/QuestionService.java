package com.dev.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dev.adapter.QuestionAdapter;
import com.dev.dto.QuestionRequestDTO;
import com.dev.dto.QuestionResponseDTO;
import com.dev.models.Question;
import com.dev.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;


@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;


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

    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm, int offset, int page) {
        return questionRepository.findByTitleOrContentContainingIgnoreCase(searchTerm, PageRequest.of(offset, page))
        .map(QuestionAdapter::toQuestionResponseDTO)
        .doOnError(error -> System.out.println("Error searching questions: " + error))
        .doOnComplete(() -> System.out.println("Questions searched successfully"));
    }

    public Flux<QuestionResponseDTO> getAllQuestions(String cursor,int size){
        Pageable pageable = PageRequest.of(0,size);
        return null;
    }
}