package com.dev.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.dev.config.KafkaConfig;
import com.dev.events.ViewCountEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaEventProducers {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishViewCountEvent(ViewCountEvent viewCountEvent){
        try {
            System.out.println("Publishing view count event for target ID: " + viewCountEvent.getTargetId());
            
            kafkaTemplate.send(KafkaConfig.TOPIC_NAME, viewCountEvent.getTargetId(), viewCountEvent)
                .whenComplete((result, err) -> {
                    if (err != null) {
                        System.err.println("Failed to publish view count event for ID " + 
                        viewCountEvent.getTargetId() + ": " + err.getMessage());
                        err.printStackTrace();
                    } else {
                        System.out.println("Successfully published view count event for ID: " + 
                        viewCountEvent.getTargetId() + " to topic: " + KafkaConfig.TOPIC_NAME);
                    }
                });
                
        } catch (Exception e) {
            System.err.println("Critical error in publishViewCountEvent: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
