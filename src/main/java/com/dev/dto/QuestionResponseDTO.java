package com.dev.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
public class QuestionResponseDTO {
    
    
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String id;
    
}
