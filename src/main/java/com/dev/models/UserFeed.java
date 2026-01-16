package com.dev.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
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
@Document(collection = "user_feeds")
public class UserFeed {

    @Id
    private String id;

    @Indexed
    private String userId;

    private List<String> feedItemIds;

    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    private boolean isActive=true;

    private int maxItems=10; //keep last 10 items

}
