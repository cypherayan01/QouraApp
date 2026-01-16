package com.dev.adapter;

import com.dev.dto.AnswerResponseDTO;
import com.dev.models.Answer;
import com.dev.models.User;

public class AnswerAdapter {

    public static AnswerResponseDTO mapToResponseDTO(Answer answer) {
        return AnswerResponseDTO.builder()
            .id(answer.getId())
            .content(answer.getContent())
            .questionId(answer.getQuestionId())
            .createdAt(answer.getCreatedAt())
            .updatedAt(answer.getUpdatedAt())
            .build();
    }

    public static AnswerResponseDTO mapToResponseDTO(Answer answer, User user) {
        return AnswerResponseDTO.builder()
            .id(answer.getId())
            .content(answer.getContent())
            .questionId(answer.getQuestionId())
            .createdAt(answer.getCreatedAt())
            .updatedAt(answer.getUpdatedAt())
            // User details
            .userId(user.getId())
            .username(user.getUsername())
            .displayName(user.getDisplayName())
            .profilePictureUrl(user.getProfilePictureUrl())
            .likeCount(0L)
            .build();
    }

    public static AnswerResponseDTO mapToResponseDTO(Answer answer, User user, String questionTitle) {
        return AnswerResponseDTO.builder()
            .id(answer.getId())
            .content(answer.getContent())
            .questionId(answer.getQuestionId())
            .createdAt(answer.getCreatedAt())
            .updatedAt(answer.getUpdatedAt())
            // User details
            .userId(user.getId())
            .username(user.getUsername())
            .displayName(user.getDisplayName())
            .profilePictureUrl(user.getProfilePictureUrl())
            .likeCount(0L)
            // Question context
            .questionTitle(questionTitle)
            .build();
    }
}
