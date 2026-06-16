package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CreateUserRequest {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 255, message = "Слишком длинное имя")
    private String name;
    @Email(message = "Email должен быть в корректном формате")
    @NotBlank(message = "Email не может быть пустым")
    @Size(max = 512, message = "Слишком длинный email")
    private String email;
}
