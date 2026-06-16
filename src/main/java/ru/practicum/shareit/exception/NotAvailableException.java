package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotAvailableException extends RuntimeException {
    private final long itemId;

    public NotAvailableException(long itemId) {
        super(String.format("предмет с id=%d недоступен", itemId));
        this.itemId = itemId;
    }
}
