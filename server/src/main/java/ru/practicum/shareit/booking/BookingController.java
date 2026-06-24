package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.List;

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

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                         @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(sharerId, request));
    }

    @PatchMapping(ID_BOOKING)
    public ResponseEntity<BookingResponse> approveBooking(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                          @PathVariable long bookingId,
                                                          @RequestParam(name = PARAM_APPROVED) boolean approved) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.approveBooking(bookingId, sharerId, approved));
    }

    @GetMapping(ID_BOOKING)
    public ResponseEntity<BookingResponse> getBooking(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                      @PathVariable long bookingId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getBooking(bookingId, sharerId));
    }

    @GetMapping()
    public ResponseEntity<List<BookingResponse>> getAllUserBookings(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                                    @RequestParam(
                                                                            name = PARAM_STATE,
                                                                            required = false,
                                                                            defaultValue = "ALL"
                                                                    ) BookingSearchState state) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getAllUserBookings(sharerId, state));
    }

    @GetMapping(URL_OWNER)
    public ResponseEntity<List<BookingResponse>> getAllUserItemBookings(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                                        @RequestParam(
                                                                                name = PARAM_STATE,
                                                                                required = false,
                                                                                defaultValue = "ALL"
                                                                        ) BookingSearchState state) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getAllUserItemBookings(sharerId, state));
    }
}
