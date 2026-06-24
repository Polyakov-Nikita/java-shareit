package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@ToString
public class GetItemRequestResponse {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<RequestedItem> items;
}
