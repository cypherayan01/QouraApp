package com.dev.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "feed_items")
public class FeedItem {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private FeedItemType type;

    private String contentId;

    private String title;
    private String content;
    private String questionId;
    
    @CreatedDate
    private LocalDateTime createdAt;

    private Long engagementScore = 0L; //likes + answers + views

    //Embedded User Info for quick access
    private String authorId;
    private String authorUsername;
    private String authorDisplayName;
    private String authorProfilePictureUrl;
}

