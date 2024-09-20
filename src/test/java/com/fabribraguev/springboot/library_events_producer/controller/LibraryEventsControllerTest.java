package com.fabribraguev.springboot.library_events_producer.controller;

import com.fabribraguev.springboot.library_events_producer.domain.LibraryEvent;
import com.fabribraguev.springboot.library_events_producer.producer.LibraryEventsProducer;
import com.fabribraguev.springboot.library_events_producer.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


//Test Slice
@WebMvcTest(LibraryEventsController.class)
class LibraryEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //Given
        String json = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());
        when(libraryEventsProducer.sendLibraryEvent3(isA(LibraryEvent.class)))
                .thenReturn(null);
        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent3").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //Then
    }
    @Test
    void postLibraryEvent_4xx() throws Exception {
        //Given
        String json = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());
        when(libraryEventsProducer.sendLibraryEvent3(isA(LibraryEvent.class)))
                .thenReturn(null);

        String expectedErrorMessage = "book.bookId - must not be null, book.bookName - must not be blank";
        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent3").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));

        //Then
    }
}