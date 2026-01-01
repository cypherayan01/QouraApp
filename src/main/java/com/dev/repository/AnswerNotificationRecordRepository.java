package com.dev.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.dev.models.AnswerNotificationRecord;

import reactor.core.publisher.Flux;

@Repository
public interface AnswerNotificationRecordRepository extends ReactiveMongoRepository<AnswerNotificationRecord, String> {
    
    Flux<AnswerNotificationRecord> findByProcessedAtAfterOrderByProcessedAtDesc(LocalDateTime after, Pageable pageable);
    
    Flux<AnswerNotificationRecord> findByProcessedAtAfterOrderByProcessedAtDesc(LocalDateTime after);
    
    Flux<AnswerNotificationRecord> findByQuestionIdAndProcessedAtAfterOrderByProcessedAtDesc(String questionId, LocalDateTime after);
    
    Flux<AnswerNotificationRecord> findByNotificationTypeAndProcessedAtAfterOrderByProcessedAtDesc(String notificationType, LocalDateTime after);
    
    @Query("{ 'processedAt': { $gte: ?0 }, $or: [ { 'answererUserId': ?1 }, { 'questionOwnerUserId': ?1 } ] }")
    Flux<AnswerNotificationRecord> findByProcessedAtAfterAndUserInvolvedOrderByProcessedAtDesc(LocalDateTime after, String userId);
}