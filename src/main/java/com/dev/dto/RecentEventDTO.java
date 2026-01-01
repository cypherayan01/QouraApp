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
public class RecentEventDTO {
    private String eventId;
    private String answerId;
    private String questionId;
    private String answerContentPreview;
    private String notificationType;
    private String answererUserId;
    private String questionOwnerUserId;
    private LocalDateTime processedAt;
    private LocalDateTime originalTimestamp;
    private String status;
}