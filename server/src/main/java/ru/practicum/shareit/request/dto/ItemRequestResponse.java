package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class ItemRequestResponse {
    private long id;
    private String description;
    private LocalDateTime created;
}
