package com.dev.services;

import com.dev.dto.QuestionRequestDTO;
import com.dev.dto.QuestionResponseDTO;


import reactor.core.publisher.Mono;

public interface IQuestionService {

    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO question);

}
