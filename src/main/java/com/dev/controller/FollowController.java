package com.dev.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.models.User;
import com.dev.services.FollowService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "Follow management APIs")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{followingId}")
    public Mono<ResponseEntity<String>> followUser(@PathVariable("followingId") String followingId,
            @RequestHeader("X-User-Id") String followerId
    ){
        return followService.followUser(followerId, followingId)
            .map(follow -> ResponseEntity.ok("Successfully followed user."))
            .onErrorReturn(ResponseEntity.badRequest().body("Failed to follow user."));
    }
    @DeleteMapping("/{followingId}")
      public Mono<ResponseEntity<String>> unfollowUser(
          @PathVariable("followingId") String followingId,
          @RequestHeader("X-User-Id") String followerId) {

          return followService.unfollowUser(followerId, followingId)
              .then(Mono.just(ResponseEntity.ok("Successfully unfollowed user")));
      }

      @GetMapping("/{userId}/followers")
      public Flux<User> getFollowers(@PathVariable("userId") String userId){
        return followService.getFollowers(userId);
      }
      @GetMapping("/{userId}/following")
      public Flux<User> getFollowing(@PathVariable("userId") String userId){
        return followService.getFollowing(userId);
      }
      

      @GetMapping("/{userId}/stats")
      public Mono<Map<String, Long>> getFollowStats(@PathVariable("userId") String userId) {
          return Mono.zip(
              followService.getFollowersCount(userId),
              followService.getFollowingCount(userId)
          ).map(tuple -> Map.of(
              "followers", tuple.getT1(),
              "following", tuple.getT2()
          ));
      }
}
