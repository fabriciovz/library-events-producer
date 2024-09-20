package com.fabribraguev.springboot.library_events_producer.domain;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryEvent {
    Integer libraryEventId;
    LibraryEventType libraryEventType;
    @NotNull
    @Valid
    Book book;
}
