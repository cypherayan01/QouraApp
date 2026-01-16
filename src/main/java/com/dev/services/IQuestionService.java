package com.dev.services;

import com.dev.dto.QuestionRequestDTO;
import com.dev.dto.QuestionResponseDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IQuestionService {

    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO question,String userId);

    public Flux<QuestionResponseDTO> searchQuestions(String query, int offset, int page);

    public Flux<QuestionResponseDTO> getAllQuestions(String cursor, int size);

    public Mono<QuestionResponseDTO> getQuestionById(String id);

    

}
