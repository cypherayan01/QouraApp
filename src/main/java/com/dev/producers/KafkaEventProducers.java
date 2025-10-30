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
        kafkaTemplate.send(KafkaConfig.TOPIC_NAME,viewCountEvent.getTargetId(),viewCountEvent)
        .whenComplete((result,err)->{
            if(err!=null){
                System.out.println("Error publishing view count event :"+ err.getMessage());
            }
        });
    }
}
