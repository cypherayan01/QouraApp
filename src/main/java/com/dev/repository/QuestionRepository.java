package com.dev.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.dev.models.Question;

public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {
    
}

