package com.dev.services;

import com.dev.dto.AnswerRequestDTO;
import com.dev.dto.AnswerResponseDTO;

import reactor.core.publisher.Mono;

public interface IAnswerService {

    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answer);
    public Mono<AnswerResponseDTO> getAnswerById(String id);
}
