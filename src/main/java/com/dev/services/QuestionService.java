package com.dev.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;


import com.dev.dto.QuestionRequestDTO;
import com.dev.dto.QuestionResponseDTO;
import com.dev.models.Question;
import com.dev.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    // public QuestionService(QuestionRepository questionRepository) {
    //     this.questionRepository = questionRepository;
    // }

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
}