package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String name;

    public interface DescriptionUpdate {
    }

    @NotBlank(message = "Описание не может быть пустым", groups = DescriptionUpdate.class)
    private String description;

    public interface AvailableUpdate {
    }

    @NotNull(message = "Должна быть информация о доступности вещи", groups = AvailableUpdate.class)
    private Boolean available;
}
