package com.dev.services;

import com.dev.dto.QuestionRequestDTO;
import com.dev.dto.QuestionResponseDTO;


import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface IQuestionService {

    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO question);

    public Flux<QuestionResponseDTO> searchQuestions(String query, int offset, int page);

}
