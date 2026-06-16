package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class BookingResponse {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booker booker;
    private BookingItem item;
    private BookingStatus status;
}
