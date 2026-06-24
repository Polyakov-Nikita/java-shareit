package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CreateItemRequest {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 255, message = "Слишком длинное имя")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 512, message = "Слишком длинное описание")
    private String description;
    @NotNull(message = "Должна быть информация о доступности вещи")
    private Boolean available;
    private Long requestId;
}
