package com.dev.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.dev.models.FeedItemType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponseDTO {
    private List<FeedItemDTO> feedItems;
    private String nextCursor;
    private boolean hasMore;
    private int totalItems;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class FeedItemDTO {
    private String id;
      private FeedItemType type;
      private String title;
      private String content;
      private String contentId;
      private String questionId;
      private LocalDateTime createdAt;
      private Long engagementScore;

      // Author info
      private String authorId;
      private String authorUsername;
      private String authorDisplayName;
      private String authorProfilePictureUrl;
}


