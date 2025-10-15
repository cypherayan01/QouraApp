package com.dev.adapter;

import com.dev.dto.QuestionResponseDTO;
import com.dev.models.Question;

public class QuestionAdapter {

    public static QuestionResponseDTO toQuestionResponseDTO(Question question) {
        return QuestionResponseDTO.builder()
            .id(question.getId())
            .title(question.getTitle())
            .content(question.getContent())
            .createdAt(question.getCreatedAt())
            .build();
    } 

}
