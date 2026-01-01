package com.dev.consumers;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dev.config.KafkaConfig;
import com.dev.events.AnswerNotificationEvent;
import com.dev.events.ViewCountEvent;
import com.dev.models.AnswerNotificationRecord;
import com.dev.repository.AnswerNotificationRecordRepository;
import com.dev.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final QuestionRepository questionRepository;
    private final AnswerNotificationRecordRepository notificationRecordRepository;

    
    @KafkaListener(topics = KafkaConfig.TOPIC_NAME,
     groupId = "view-count-consumer", 
     containerFactory = "kafkaListenerContainerFactory")
    public void handleViewCount(ViewCountEvent viewCountEvent) {
        try {
            System.out.println("Processing view count event for target ID: " + viewCountEvent.getTargetId());
            
            questionRepository.findById(viewCountEvent.getTargetId())
                .doOnNext(question -> {
                    System.out.println("Found question ID: " + question.getId() + ", current views: " + question.getViews());
                    question.setViews(question.getViews() == null ? 1 : question.getViews() + 1);
                })
                .flatMap(question -> {
                    System.out.println("Incrementing view count for question ID: " + question.getId());
                    return questionRepository.save(question);
                })
                .doOnSuccess(updatedQuestion -> 
                    System.out.println("Successfully updated view count for question ID: " + updatedQuestion.getId() + 
                                     " to " + updatedQuestion.getViews()))
                .doOnError(error -> 
                    System.err.println("Error updating view count for ID " + viewCountEvent.getTargetId() + 
                                     ": " + error.getMessage()))
                .onErrorResume(error -> {
                    System.err.println("Recovering from view count update error, continuing: " + error.getMessage());
                    return Mono.empty();
                })
                .subscribe(
                    success -> System.out.println("View count processing completed for ID: " + viewCountEvent.getTargetId()),
                    error -> System.err.println("Final error in view count processing: " + error.getMessage())
                );
                
        } catch (Exception e) {
            System.err.println("Critical error in handleViewCount: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = KafkaConfig.ANSWER_NOTIFICATION_TOPIC,
     groupId = "notification-consumer", 
     containerFactory = "kafkaListenerContainerFactory")
    public void handleAnswerNotification(AnswerNotificationEvent notificationEvent) {
        try {
            System.out.println("Processing answer notification for answer ID: " + notificationEvent.getAnswerId());
            
            // Store the notification event in database for later retrieval
            AnswerNotificationRecord record = AnswerNotificationRecord.builder()
                .answerId(notificationEvent.getAnswerId())
                .questionId(notificationEvent.getQuestionId())
                .answerContentPreview(notificationEvent.getAnswerContent())
                .answererUserId(notificationEvent.getAnswererUserId())
                .questionOwnerUserId(notificationEvent.getQuestionOwnerUserId())
                .notificationType(notificationEvent.getNotificationType())
                .originalTimestamp(notificationEvent.getTimestamp())
                .processedAt(notificationEvent.getTimestamp())
                .status("PROCESSED")
                .build();
            
            notificationRecordRepository.save(record)
                .doOnSuccess(savedRecord -> {
                    System.out.println("Notification event stored with ID: " + savedRecord.getId());
                    System.out.println("- Answer ID: " + notificationEvent.getAnswerId());
                    System.out.println("- Question ID: " + notificationEvent.getQuestionId());
                    System.out.println("- Notification Type: " + notificationEvent.getNotificationType());
                    
                    // Here you would typically:
                    // 1. Send email/push notification to question owner
                    // 2. Send real-time updates via WebSocket
                    // 3. Update user activity feed
                })
                .doOnError(error -> {
                    System.err.println("Failed to store notification record: " + error.getMessage());
                    // Could update record status to "FAILED" here
                })
                .subscribe();
            
        } catch (Exception e) {
            System.err.println("Critical error in handleAnswerNotification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
