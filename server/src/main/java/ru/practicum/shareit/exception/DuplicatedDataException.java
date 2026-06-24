package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class DuplicatedDataException extends RuntimeException {
    private final String objectType;
    private final String fieldName;
    private final Object value;

    public DuplicatedDataException(String objectType, String fieldName, Object value) {
        super(String.format("%s с параметром '%s'=%s уже сохранен", objectType, fieldName, value.toString()));
        this.objectType = objectType;
        this.fieldName = fieldName;
        this.value = value;
    }
}
