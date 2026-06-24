package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CreateCommentRequest {
    private String text;
}
