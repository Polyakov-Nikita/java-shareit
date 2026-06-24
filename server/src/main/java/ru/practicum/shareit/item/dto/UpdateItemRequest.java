package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class UpdateItemRequest {
    private String name;
    private String description;
    private Boolean available;
}
