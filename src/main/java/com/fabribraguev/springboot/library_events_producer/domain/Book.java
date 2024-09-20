package com.fabribraguev.springboot.library_events_producer.domain;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @NotNull
    Integer bookId;
    @NotBlank
    String bookName;
    @NotBlank
    String bookAuthor;
}
