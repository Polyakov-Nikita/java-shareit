package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class RequestedItem {
    private long itemId;
    private String name;
    private long ownerId;
}
