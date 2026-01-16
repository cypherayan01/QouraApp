package com.dev.adapter;

import com.dev.dto.QuestionResponseDTO;
import com.dev.models.Question;
import com.dev.models.User;

public class QuestionAdapter {

    public static QuestionResponseDTO toQuestionResponseDTO(Question question,User user) {
        return QuestionResponseDTO.builder()
          .id(question.getId())
          .title(question.getTitle())
          .content(question.getContent())
          .views(question.getViews())
          .createdAt(question.getCreatedAt())
          .updatedAt(question.getUpdatedAt())
          .userId(user.getId())
          .username(user.getUsername())
          .displayName(user.getDisplayName())
          .profilePictureUrl(user.getProfilePictureUrl())
          .answerCount(0L) // Calculate if needed
          .likeCount(0L)   // Calculate if needed
          .build();
    } 

}
