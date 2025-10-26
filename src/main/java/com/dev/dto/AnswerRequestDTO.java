package com.dev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequestDTO {

    @NotBlank(message = "Content cannot be blank")
    @Size(min=10,max =1000, message = "Content must be between 10 and 1000 characters")
    private String content;

    @NotBlank(message = "Content cannot be blank")
    private String questionId;

}
