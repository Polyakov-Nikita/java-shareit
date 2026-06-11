package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String objectType;
    private final long id;

    public NotFoundException(String objectType, long id) {
        super(String.format("%s с id=%d отсутствует", objectType, id));
        this.objectType = objectType;
        this.id = id;
    }
}
