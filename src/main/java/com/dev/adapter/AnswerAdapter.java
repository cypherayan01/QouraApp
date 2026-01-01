package com.dev.adapter;

import com.dev.dto.AnswerResponseDTO;
import com.dev.models.Answer;

public class AnswerAdapter {

    public static AnswerResponseDTO mapToResponseDTO(Answer answer) {
        return AnswerResponseDTO.builder()
            .id(answer.getId())
            .content(answer.getContent())
            .createdAt(answer.getCreatedAt())
            .updatedAt(answer.getUpdatedAt())
            .build();
    }

}
