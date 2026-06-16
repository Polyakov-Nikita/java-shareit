package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class CommentResponse {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
