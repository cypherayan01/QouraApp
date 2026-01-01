package com.dev.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.dev.models.Answer;

import reactor.core.publisher.Flux;

@Repository
public interface AnswerRepository extends  ReactiveMongoRepository<Answer, String> {
    
    Flux<Answer> findByQuestionId(String questionId);
    
}
