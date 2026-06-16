package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class CreateBookingRequest {
    @NotNull(message = "требуется id предмета")
    private Long itemId;
    @NotNull(message = "дата начала не может быть пустой")
    @FutureOrPresent(message = "дата начала не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "дата конца не может быть пустой")
    @FutureOrPresent(message = "дата конца не может быть в прошлом")
    private LocalDateTime end;
}
