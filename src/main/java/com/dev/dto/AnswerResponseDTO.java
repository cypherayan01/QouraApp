package com.dev.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponseDTO {
     private String id;
      private String content;
      private String questionId;
      private LocalDateTime createdAt;
      private LocalDateTime updatedAt;

      // User details
      private String userId;
      private String username;
      private String displayName;
      private String profilePictureUrl;

      // Answer stats
      @Builder.Default
      private Long likeCount = 0L;

      // Optional: Question context for feed
      private String questionTitle;


}
