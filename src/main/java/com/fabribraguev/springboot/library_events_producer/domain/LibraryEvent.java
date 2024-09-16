package com.fabribraguev.springboot.library_events_producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryEvent {
    Integer libraryEventId;
    LibraryEventType libraryEventType;
    Book book;
}
