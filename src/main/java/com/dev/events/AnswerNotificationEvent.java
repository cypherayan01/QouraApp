package com.dev.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerNotificationEvent {
    private String answerId;
    private String questionId;
    private String answerContent;
    private String answererUserId;
    private String questionOwnerUserId;
    private String notificationType;
    private LocalDateTime timestamp;
}