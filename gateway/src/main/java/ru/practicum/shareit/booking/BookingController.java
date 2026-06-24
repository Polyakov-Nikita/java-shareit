package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

@RestController
@RequestMapping(path = BookingController.URL_BASE)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BookingController {
    public static final String URL_BASE = "/bookings";
    public static final String URL_OWNER = "/owner";
    public static final String PARAM_APPROVED = "approved";
    public static final String PARAM_STATE = "state";
    public static final String ID_BOOKING = "/{bookingId}";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                                @RequestBody @Valid CreateBookingRequest request) {
        return bookingClient.createBooking(sharerId, request);
    }

    @PatchMapping(ID_BOOKING)
    public ResponseEntity<Object> approveBooking(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam(name = PARAM_APPROVED) boolean approved) {
        return bookingClient.approveBooking(sharerId, bookingId, approved);
    }

    @GetMapping(ID_BOOKING)
    public ResponseEntity<Object> getBooking(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                             @PathVariable long bookingId) {
        return bookingClient.getBooking(sharerId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                                     @RequestParam(
                                                             name = PARAM_STATE,
                                                             required = false,
                                                             defaultValue = "ALL"
                                                     ) BookingSearchState state) {
        return bookingClient.getAllUserBookings(sharerId, state);
    }

    @GetMapping(URL_OWNER)
    public ResponseEntity<Object> getAllUserItemBookings(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                                         @RequestParam(
                                                                 name = PARAM_STATE,
                                                                 required = false,
                                                                 defaultValue = "ALL"
                                                         ) BookingSearchState state) {
        return bookingClient.getAllUserItemBookings(sharerId, state);
    }
}
