package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotSharerException extends RuntimeException {
    private final long userId;
    private final long itemId;

    public NotSharerException(long userId, long itemId) {
        super(String.format("пользователь с id=%d не является владельцем предмета с id=%d", userId, itemId));
        this.userId = userId;
        this.itemId = itemId;
    }
}
