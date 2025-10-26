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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
