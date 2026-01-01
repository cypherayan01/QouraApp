package com.dev.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "answer_notification_records")
public class AnswerNotificationRecord {
    @Id
    private String id;
    
    @Indexed
    private String answerId;
    
    @Indexed
    private String questionId;
    
    private String answerContentPreview;
    private String answererUserId;
    private String questionOwnerUserId;
    private String notificationType;
    
    private LocalDateTime originalTimestamp;
    
    @CreatedDate
    @Indexed
    private LocalDateTime processedAt;
    
    private String status; // PROCESSED, FAILED, etc.
}