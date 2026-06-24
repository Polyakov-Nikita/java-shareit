package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ForbiddenAccessException extends RuntimeException {
    private final long userId;
    private final String details;

    public ForbiddenAccessException(long userId, String details) {
        super(String.format("доступ к этим данным закрыт для пользователя с id=%d, причина: %s", userId, details));
        this.userId = userId;
        this.details = details;
    }
}
