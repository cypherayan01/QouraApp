package com.dev.services;

import com.dev.dto.AnswerRequestDTO;
import com.dev.dto.AnswerResponseDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAnswerService {

    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answer,String userId);
    public Mono<AnswerResponseDTO> getAnswerById(String id);
    public Flux<AnswerResponseDTO> getAllAnswers();
    public Flux<AnswerResponseDTO> getAnswersByQuestionId(String questionId);
    public Mono<AnswerResponseDTO> updateAnswer(String id, AnswerRequestDTO answerRequestDTO);
    public Mono<Void> deleteAnswer(String id);
}
