package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotOwnerException extends RuntimeException {
    private final long userId;
    private final long itemId;

    public NotOwnerException(long userId, long itemId) {
        super(String.format("пользователь с id=%d не является владельцем предмета с id=%d", userId, itemId));
        this.userId = userId;
        this.itemId = itemId;
    }
}
