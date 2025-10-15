package com.dev.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.dev.models.Question;

import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Flux;

public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {

    @Query("{ '$or': [ { 'title': { $regex: ?0, $options: 'i'} }, { 'content' : { $regex: ?0, $options: 'i' } } ] }")
    Flux<Question> findByTitleOrContentContainingIgnoreCase(String searchTerm, Pageable pageable);
    
}

