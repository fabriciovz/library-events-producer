package com.fabribraguev.springboot.library_events_producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    Integer bookId;
    String bookName;
    String bookAuthor;
}
