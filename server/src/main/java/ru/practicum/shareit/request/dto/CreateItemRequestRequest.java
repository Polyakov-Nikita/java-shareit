package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CreateItemRequestRequest {
    private String description;
}
