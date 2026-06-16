package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {
    public Booking toBooking(Item item, User booker, CreateBookingRequest request) {
        return Booking.builder()
                .start(request.getStart())
                .end(request.getEnd())
                .item(item)
                .booker(booker)
                .build();
    }

    public BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(getBookingItem(booking))
                .booker(getBooker(booking))
                .status(booking.getStatus())
                .build();
    }

    private BookingItem getBookingItem(Booking booking) {
        Item item = booking.getItem();
        return BookingItem.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    private Booker getBooker(Booking booking) {
        return Booker.builder()
                .id(booking.getBooker().getId())
                .build();
    }
}
