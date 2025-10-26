package com.dev.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.dev.models.Like;

@Repository
public interface LikeRepository extends ReactiveMongoRepository<Like, String> {

}
