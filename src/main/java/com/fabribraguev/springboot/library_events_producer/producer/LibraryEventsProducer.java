package com.fabribraguev.springboot.library_events_producer.producer;

import com.fabribraguev.springboot.library_events_producer.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class LibraryEventsProducer {
    @Value("${spring.kafka.topic}")
    public String topic;
   private KafkaTemplate<Integer,String> kafkaTemplate;

   private final ObjectMapper objectMapper;

   public LibraryEventsProducer(KafkaTemplate<Integer,String> kafkaTemplate,ObjectMapper objectMapper){
       this.kafkaTemplate=kafkaTemplate;
       this.objectMapper=objectMapper;

   }

   public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
       Integer key = libraryEvent.getLibraryEventId();
       String value = objectMapper.writeValueAsString(libraryEvent);
       ListenableFuture<SendResult<Integer, String>> send = kafkaTemplate.send(topic, key, value);

       return send.completable().whenComplete((sendResult, throwable) -> {
           if(throwable!=null){
                handleFailure(key,value,throwable);
           }else {
                handlesuccess(key,value,sendResult);
           }
       });

   }

    private void handlesuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
       log.info("Message sent successfully for the key : {} and the value : {}, partition is : {}", key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
       log.error("Error sending the message and the exception is {} ", throwable.getMessage(), throwable);
    }
}
