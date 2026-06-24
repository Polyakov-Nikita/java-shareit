package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.test.ControllerTest;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.handler.ErrorHandler;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class BookingControllerTest extends ControllerTest {
    private static final LocalDateTime NOW = LocalDateTime.now();

    @InjectMocks
    private BookingController controller;
    @Mock
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void createBooking_StatusCreated() {
        // Arrange
        long sharerId = 1L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                .itemId(1L)
                .start(NOW.plusDays(1))
                .end(NOW.plusDays(2))
                .build();

        // Act
        ResultActions result = performPost(sharerId, BookingController.URL_BASE, request);

        // Assert
        expectStatusCreated(result);
        expectMethodCall(bookingService, service ->
                service.createBooking(Mockito.eq(sharerId), Mockito.any(CreateBookingRequest.class)));
    }

    @Test
    public void approveBooking_StatusOK() {
        // Arrange
        long sharerId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        // Act
        ResultActions result = performPatch(sharerId, createApproveBookingUrl(bookingId, approved));

        // Assert
        expectStatusOk(result);
        expectMethodCall(bookingService, service ->
                service.approveBooking(Mockito.eq(bookingId), Mockito.eq(sharerId), Mockito.eq(approved)));
    }

    @Test
    public void getBooking_StatusOK() {
        // Arrange
        long sharerId = 1L;
        long bookingId = 1L;

        // Act
        ResultActions result = performGet(sharerId, createGetBookingUrl(bookingId));

        // Assert
        expectStatusOk(result);
        expectMethodCall(bookingService, service ->
                service.getBooking(Mockito.eq(bookingId), Mockito.eq(sharerId)));
    }

    @Test
    public void getAllUserBookings_StatusOK() {
        // Arrange
        long sharerId = 1L;
        BookingSearchState state = BookingSearchState.CURRENT;

        // Act
        ResultActions result = performGet(sharerId, createGetAllUserBookingsUrl(state));

        // Assert
        expectStatusOk(result);
        expectMethodCall(bookingService, service ->
                service.getAllUserBookings(Mockito.eq(sharerId), Mockito.eq(state)));
    }

    @Test
    public void getAllUserItemBookings_StatusOK() {
        // Arrange
        long sharerId = 1L;
        BookingSearchState state = BookingSearchState.CURRENT;

        // Act
        ResultActions result = performGet(sharerId, createGetAllUserItemBookingsUrl(state));

        // Assert
        expectStatusOk(result);
        expectMethodCall(bookingService, service ->
                service.getAllUserItemBookings(Mockito.eq(sharerId), Mockito.eq(state)));
    }

    private String createApproveBookingUrl(long bookingId, boolean approved) {
        return String.format("%s/%d?%s=%s",
                BookingController.URL_BASE, bookingId, BookingController.PARAM_APPROVED, approved);
    }

    private String createGetBookingUrl(long bookingId) {
        return String.format("%s/%d",
                BookingController.URL_BASE, bookingId);
    }

    private String createGetAllUserBookingsUrl(BookingSearchState state) {
        return String.format("%s?%s=%s",
                BookingController.URL_BASE, BookingController.PARAM_STATE, state);
    }

    private String createGetAllUserItemBookingsUrl(BookingSearchState state) {
        return String.format("%s%s?%s=%s",
                BookingController.URL_BASE, BookingController.URL_OWNER, BookingController.PARAM_STATE, state);
    }
}
