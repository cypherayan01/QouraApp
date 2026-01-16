package com.dev.models;

import java.time.LocalDateTime;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
@Document(collection = "follows")
@CompoundIndex(def="{'followerId': 1, 'followingId': 1}", unique = true)
public class Follow {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Follower ID is required")
    private String followerId;

    @Indexed
    @NotBlank(message = "Following ID is required")
    private String followingId;

    private LocalDateTime createdAt;
}
