package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.test.JsonTest;

import java.time.LocalDateTime;

public class CreateBookingRequestJsonTest extends JsonTest<CreateBookingRequest> {
    @Test
    public void serialize() {
        // Arrange
        LocalDateTime start = NOW.plusDays(1);
        LocalDateTime end = NOW.plusDays(2);
        CreateBookingRequest dto = CreateBookingRequest.builder()
                .start(start)
                .end(end)
                .build();

        // Act
        JsonContent<CreateBookingRequest> actual = writeContent(dto);

        // Assert
        Assertions.assertThat(actual).extractingJsonPathValue("$.start").isEqualTo(start.format(FORMATTER));
        Assertions.assertThat(actual).extractingJsonPathValue("$.end").isEqualTo(end.format(FORMATTER));
    }
}
