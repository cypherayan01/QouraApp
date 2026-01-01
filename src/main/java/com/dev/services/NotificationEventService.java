package com.dev.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.dev.dto.RecentEventDTO;
import com.dev.dto.RecentEventsResponseDTO;
import com.dev.models.AnswerNotificationRecord;
import com.dev.repository.AnswerNotificationRecordRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationEventService {

    private final AnswerNotificationRecordRepository notificationRecordRepository;

    public Mono<RecentEventsResponseDTO> getRecentEvents(Integer hours, Integer limit, 
                                                        String questionId, String notificationType, String userId) {
        
        int hoursBack = hours != null ? hours : 24;
        int maxLimit = limit != null ? Math.min(limit, 1000) : 100; // Cap at 1000
        
        LocalDateTime fromTime = LocalDateTime.now().minusHours(hoursBack);
        LocalDateTime toTime = LocalDateTime.now();
        
        System.out.println(" DEBUG - Searching for events:");
        System.out.println("   From: " + fromTime);
        System.out.println("   To: " + toTime);
        System.out.println("   Hours back: " + hoursBack);
        System.out.println("   Max limit: " + maxLimit);
        
        
        return notificationRecordRepository.count()
            .doOnNext(count -> System.out.println(" Total records in collection: " + count))
            .then(getFilteredEvents(fromTime, questionId, notificationType, userId, maxLimit)
                .collectList()
                .doOnNext(records -> System.out.println("Found " + records.size() + " matching records"))
                .flatMap(records -> {
                    // Convert to DTOs
                    var eventDTOs = records.stream()
                        .map(this::mapToEventDTO)
                        .toList();
                    
                    // Build response
                    return Mono.just(RecentEventsResponseDTO.builder()
                        .events(eventDTOs)
                        .totalCount(eventDTOs.size())
                        .timeRange(RecentEventsResponseDTO.TimeRangeDTO.builder()
                            .from(fromTime)
                            .to(toTime)
                            .build())
                        .build());
                }));
    }

    private Flux<AnswerNotificationRecord> getFilteredEvents(
            LocalDateTime fromTime, String questionId, String notificationType, String userId, int limit) {
        
        PageRequest pageRequest = PageRequest.of(0, limit);
        
        // Apply filters based on provided parameters
        if (questionId != null && !questionId.isEmpty()) {
            return notificationRecordRepository
                .findByQuestionIdAndProcessedAtAfterOrderByProcessedAtDesc(questionId, fromTime)
                .take(limit);
        }
        
        if (notificationType != null && !notificationType.isEmpty()) {
            return notificationRecordRepository
                .findByNotificationTypeAndProcessedAtAfterOrderByProcessedAtDesc(notificationType, fromTime)
                .take(limit);
        }
        
        if (userId != null && !userId.isEmpty()) {
            return notificationRecordRepository
                .findByProcessedAtAfterAndUserInvolvedOrderByProcessedAtDesc(fromTime, userId)
                .take(limit);
        }
        
        // Default: get all recent events - let's also check ALL records to debug
        return notificationRecordRepository
            .findAll()
            .doOnNext(record -> System.out.println("All records - ID: " + record.getId() + 
                ", processedAt: " + record.getProcessedAt() + 
                ", answerId: " + record.getAnswerId()))
            .filter(record -> record.getProcessedAt() != null && record.getProcessedAt().isAfter(fromTime))
            .doOnNext(record -> System.out.println(" MATCHED record: " + record.getId() + 
                ", processedAt: " + record.getProcessedAt()))
            .take(limit);
    }

    private RecentEventDTO mapToEventDTO(AnswerNotificationRecord record) {
        return RecentEventDTO.builder()
            .eventId(record.getId())
            .answerId(record.getAnswerId())
            .questionId(record.getQuestionId())
            .answerContentPreview(record.getAnswerContentPreview())
            .notificationType(record.getNotificationType())
            .answererUserId(record.getAnswererUserId())
            .questionOwnerUserId(record.getQuestionOwnerUserId())
            .processedAt(record.getProcessedAt())
            .originalTimestamp(record.getOriginalTimestamp())
            .status(record.getStatus())
            .build();
    }

    public Mono<Long> getTotalEventCount(Integer hours) {
        int hoursBack = hours != null ? hours : 24;
        LocalDateTime fromTime = LocalDateTime.now().minusHours(hoursBack);
        
        return notificationRecordRepository
            .findByProcessedAtAfterOrderByProcessedAtDesc(fromTime)
            .count();
    }
}