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
public class UpdateItemRequest {
    public interface NameUpdate {
    }

    @NotBlank(message = "Имя не может быть пустым", groups = NameUpdate.class)
    @Size(max = 255, message = "Слишком длинное имя")
    private String name;

    public interface DescriptionUpdate {
    }

    @NotBlank(message = "Описание не может быть пустым", groups = DescriptionUpdate.class)
    @Size(max = 512, message = "Слишком длинное описание")
    private String description;

    public interface AvailableUpdate {
    }

    @NotNull(message = "Должна быть информация о доступности вещи", groups = AvailableUpdate.class)
    private Boolean available;
}
