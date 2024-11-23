package com.fabribraguev.springboot.library_events_producer.producer;

import com.fabribraguev.springboot.library_events_producer.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    //ASync approach
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

   //Sync approach
    public SendResult<Integer, String> sendLibraryEvent2(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        //1. Blocking call - get metadata about the kafka cluster -> max.block.ms (60000 ms defulut - 60s)
        //2. Block and wait until the message is sent to the kafka
        SendResult<Integer, String> sendResult = kafkaTemplate.send(topic, key, value)
                //.get()
                .get(3, TimeUnit.SECONDS);
        handlesuccess(key,value,sendResult);
        return sendResult;
    }


    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent3(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value);

        ListenableFuture<SendResult<Integer, String>> send = kafkaTemplate.send(producerRecord);

        return send.completable().whenComplete((sendResult, throwable) -> {
            if(throwable!=null){
                handleFailure(key,value,throwable);
            }else {
                handlesuccess(key,value,sendResult);
            }
        });

    }

    private ProducerRecord buildProducerRecord(Integer key, String value) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        return new ProducerRecord<>(topic,null,key,value,recordHeaders);
    }

    private void handlesuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
       log.info("Message sent successfully for the key : {} and the value : {}, partition is : {}", key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
       log.error("Error sending the message and the exception is {} ", throwable.getMessage(), throwable);
    }


}
