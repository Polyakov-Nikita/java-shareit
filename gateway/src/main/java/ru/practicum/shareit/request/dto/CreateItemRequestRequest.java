package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CreateItemRequestRequest {
    @NotBlank(message = "Запрос не может быть пустым")
    @Size(max = 512, message = "Слишком длинный запрос")
    private String description;
}
