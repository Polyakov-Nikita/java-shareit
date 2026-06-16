package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(long sharerId, CreateBookingRequest request);

    BookingResponse approveBooking(long id, long sharerId, boolean approved);

    BookingResponse getBooking(long id, long sharerId);

    List<BookingResponse> getAllUserBookings(long sharerId, BookingSearchState state);

    List<BookingResponse> getAllUserItemBookings(long sharerId, BookingSearchState state);
}
