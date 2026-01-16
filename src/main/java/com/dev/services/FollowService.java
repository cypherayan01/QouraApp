package com.dev.services;

import org.springframework.stereotype.Service;

import com.dev.models.Follow;
import com.dev.models.User;
import com.dev.repository.FollowRepository;
import com.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FollowService implements IFollowService{

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public Mono<Follow> followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
              return Mono.error(new RuntimeException("Cannot follow yourself"));
          }

          return followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
            .flatMap(existing -> Mono.<Follow>error(new RuntimeException("Already following")))
            .switchIfEmpty(
                followRepository.save(Follow.builder()
                    .followerId(followerId)
                    .followingId(followingId)
                    .build())
                    .flatMap(follow -> updateUserStats(followerId, followingId, true).then(Mono.just(follow)))
            
            );
              
             
              
    }

    @Override
    public Mono<Void> unfollowUser(String followerId, String followingId) {
        return followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
            .switchIfEmpty(Mono.error(new RuntimeException("Not following")))
            .flatMap(existing -> followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId)
                .then(updateUserStats(followerId, followingId, false)));
    } 

    @Override
    public Mono<Boolean> isFollowing(String followerId, String followingId) {
        return followRepository.findByFollowerIdAndFollowingId(followerId, followingId).hasElement();
    }
        

    @Override
    public Flux<User> getFollowers(String userId) {
       return followRepository.findByFollowingId(userId)
        .flatMap(follow -> userRepository.findById(follow.getFollowerId()));

    }

    @Override
    public Flux<User> getFollowing(String userId) {
        return followRepository.findByFollowerId(userId)
            .flatMap(follow -> userRepository.findById(follow.getFollowingId()));
    }

    @Override
    public Mono<Long> getFollowersCount(String userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Override
    public Mono<Long> getFollowingCount(String userId) {
        return followRepository.countByFollowerId(userId);
    }

    private Mono<Void> updateUserStats(String followerId, String followingId, boolean isFollow) {
          int delta = isFollow ? 1 : -1;

          Mono<Void> updateFollower = userRepository.findById(followerId)
              .flatMap(user -> {
                  user.setFollowingCount(Math.max(0, user.getFollowingCount() + delta));
                  return userRepository.save(user);
              })
              .then();

          Mono<Void> updateFollowing = userRepository.findById(followingId)
              .flatMap(user -> {
                  user.setFollowerCount(Math.max(0, user.getFollowerCount() + delta));
                  return userRepository.save(user);
              })
              .then();

          return Mono.when(updateFollower, updateFollowing);
      }
    

}
