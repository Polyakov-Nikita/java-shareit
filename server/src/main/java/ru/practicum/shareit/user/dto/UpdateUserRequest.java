package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class UpdateUserRequest {
    private String name;
    private String email;
}
