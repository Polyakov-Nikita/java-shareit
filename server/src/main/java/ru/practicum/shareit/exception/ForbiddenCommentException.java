package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ForbiddenCommentException extends RuntimeException {
    private final long userId;
    private final long itemId;

    public ForbiddenCommentException(long userId, long itemId) {
        super(String.format("пользователь с id='%d' не может комментировать вещь с id='%d', так как он её не арендовал",
                userId, itemId));
        this.userId = userId;
        this.itemId = itemId;
    }
}
