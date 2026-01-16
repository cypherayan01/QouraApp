package com.dev.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
public class QuestionResponseDTO {

    private String id;
      private String title;
      private String content;
      private Integer views;
      private LocalDateTime createdAt;
      private LocalDateTime updatedAt;

      // User details
      private String userId;
      private String username;
      private String displayName;
      private String profilePictureUrl;

      // Question stats
      @Builder.Default
      private Long answerCount = 0L;
      @Builder.Default
      private Long likeCount = 0L;

    
}
