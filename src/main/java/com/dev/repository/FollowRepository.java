package com.dev.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.dev.models.Follow;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FollowRepository extends ReactiveMongoRepository<Follow, String> {

    Mono<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);
    Flux<Follow> findByFollowerId(String followerId);
    Flux<Follow> findByFollowingId(String followingId);
    Mono<Long> countByFollowingId(String followingId);
    Mono<Long> countByFollowerId(String followerId);
    Mono<Void> deleteByFollowerIdAndFollowingId(String followerId, String followingId);

}
