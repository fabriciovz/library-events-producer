package com.fabribraguev.springboot.library_events_producer.controller;

import com.fabribraguev.springboot.library_events_producer.domain.LibraryEvent;
import com.fabribraguev.springboot.library_events_producer.domain.LibraryEventType;
import com.fabribraguev.springboot.library_events_producer.producer.LibraryEventsProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    private LibraryEventsProducer libraryEventsProducer;
    //ASync approach
    @PostMapping("/v1/libraryevent1")
    public ResponseEntity<LibraryEvent> postLibraryEvent(  //Async approach
            @RequestBody LibraryEvent libraryEvent
    ) throws JsonProcessingException {
        log.info("libraryEvent : {} ",libraryEvent.toString());
        //invoke the kafka producer
        libraryEventsProducer.sendLibraryEvent(libraryEvent);

        log.info("After sending library event"); //This log is printed before the kafka message was sent becuase of the async approach

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
    //Sync approach
    @PostMapping("/v1/libraryevent2")
    public ResponseEntity<LibraryEvent> postLibraryEvent2(  //Async approach
                                                           @RequestBody LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent : {} ",libraryEvent.toString());
        //invoke the kafka producer
        libraryEventsProducer.sendLibraryEvent2(libraryEvent);

        log.info("After sending library event"); //This log is printed before the kafka message was sent becuase of the async approach

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    //Sync approach
    @PostMapping("/v1/libraryevent3")
    public ResponseEntity<LibraryEvent> postLibraryEvent3(  //Async approach
                                                            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent : {} ",libraryEvent.toString());
        //invoke the kafka producer
        libraryEventsProducer.sendLibraryEvent3(libraryEvent);

        log.info("After sending library event"); //This log is printed before the kafka message was sent becuase of the async approach

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent3")
    public ResponseEntity<?> updatetLibraryEvent3(  //Async approach
                                                    @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent : {} ",libraryEvent.toString());
        //invoke the kafka producer
        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        libraryEventsProducer.sendLibraryEvent3(libraryEvent);

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the libraryeventID");
        }

        if (!libraryEvent.getLibraryEventType().equals(LibraryEventType.UPDATE)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        return null;
    }
}
