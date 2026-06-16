package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CreateCommentRequest {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
