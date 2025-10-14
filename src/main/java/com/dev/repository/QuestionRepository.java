package com.dev.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.dev.models.Question;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {

    Flux<Question> findByAuthorId(String authorId);

    Mono<Long> countByAuthorId(String authorId);
}
