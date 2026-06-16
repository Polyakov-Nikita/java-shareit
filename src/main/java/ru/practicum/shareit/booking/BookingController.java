package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.ItemController;

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

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestHeader(ItemController.HEADER_SHARER) long sharerId,
                                                         @RequestBody @Valid CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(sharerId, request));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> approveBooking(@RequestHeader(ItemController.HEADER_SHARER) long sharerId,
                                                          @PathVariable long bookingId,
                                                          @RequestParam(name = PARAM_APPROVED) boolean approved) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.approveBooking(bookingId, sharerId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@RequestHeader(ItemController.HEADER_SHARER) long sharerId,
                                                      @PathVariable long bookingId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBooking(bookingId, sharerId));
    }

    @GetMapping()
    public ResponseEntity<List<BookingResponse>> getAllUserBookings(@RequestHeader(ItemController.HEADER_SHARER) long sharerId,
                                                                    @RequestParam(
                                                                            name = PARAM_STATE,
                                                                            required = false,
                                                                            defaultValue = "ALL"
                                                                    ) BookingSearchState state) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllUserBookings(sharerId, state));
    }

    @GetMapping(URL_OWNER)
    public ResponseEntity<List<BookingResponse>> getAllUserItemBookings(@RequestHeader(ItemController.HEADER_SHARER) long sharerId,
                                                                        @RequestParam(
                                                                                name = PARAM_STATE,
                                                                                required = false,
                                                                                defaultValue = "ALL"
                                                                        ) BookingSearchState state) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllUserItemBookings(sharerId, state));
    }
}
