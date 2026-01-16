package com.dev.services;

import com.dev.models.Follow;
import com.dev.models.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IFollowService {
    Mono<Follow> followUser(String followerId,String followingId);
    Mono<Void> unfollowUser(String followerId,String followingId);
    Mono<Boolean> isFollowing(String followerId,String followingId);
    Flux<User> getFollowers(String userId);
    Flux<User> getFollowing(String userId);
    Mono<Long> getFollowersCount(String userId);
    Mono<Long> getFollowingCount(String userId);
    
}
