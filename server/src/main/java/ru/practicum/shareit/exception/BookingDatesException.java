package ru.practicum.shareit.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookingDatesException extends RuntimeException {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public BookingDatesException(LocalDateTime start, LocalDateTime end) {
        super(String.format("дата начала start='%s' должна быть раньше end='%s'", start, end));
        this.start = start;
        this.end = end;
    }
}
