package com.dev.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.dev.models.Answer;

@Repository
public interface AnswerRepository extends  ReactiveMongoRepository<Answer, String> {

}
