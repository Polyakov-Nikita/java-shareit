package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CreateCommentRequest {
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 512, message = "Слишком длинный текст")
    private String text;
}
