package com.dev.services;

import com.dev.models.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<User> createUser(User user);
    Mono<User> getUserById(String id);
    Mono<User> getUserByEmail(String email);
    Mono<User> updateUser(String id, User user);
    Mono<Void> deleteUser(String id);
    Mono<Boolean> userExists(String username,String email);
    Flux<User> searchUsers(String query);
    Mono<User> getUserByUsername(String username);

}
