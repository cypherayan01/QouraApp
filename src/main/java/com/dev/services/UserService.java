package com.dev.services;

import org.springframework.stereotype.Service;

import com.dev.models.User;
import com.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    @Override
    public Mono<User> createUser(User user) {
        return userExists( user.getUsername() ,user.getEmail())
            .flatMap(exists -> {
                if(exists){
                    return Mono.error(new RuntimeException("User already exists."));
                }
                
                return userRepository.save(user);
            });
    }

    @Override
    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Mono<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public Mono<User> updateUser(String id, User user) {
        return userRepository.findById(id)
        .flatMap(existingUser->{
            existingUser.setDisplayName(user.getDisplayName());
            existingUser.setBio(user.getBio());
            existingUser.setProfilePictureUrl(user.getProfilePictureUrl());
            return userRepository.save(existingUser);
        });
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }


    @Override
    public Flux<User> searchUsers(String query) {
        return userRepository.findAll()
        .filter(user -> user.getUsername().toLowerCase().contains(query.toLowerCase())|| 
            user.getDisplayName().toLowerCase().contains(query.toLowerCase())
        );
        
    }

    @Override
    public Mono<Boolean> userExists(String username, String email) {
        return userRepository.existsByUsername(username)
            .flatMap(usernameExists -> {
                if(usernameExists){
                    return Mono.just(true);
                }
                return userRepository.existsByEmail(email);
            });
    }

}


//  The remaining steps will involve:

//   Step 7: Update existing Question/Answer services to include userId
//   Step 8: Create FeedItem model for representing feed contentStep 9: Implement FeedService with pull-based feed generation
//   Step 10: Create FeedController with pagination
//   Step 11: Add Kafka events for real-time feed updates
//   Step 12: Testing and validation