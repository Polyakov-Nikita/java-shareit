package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.ControllerTest;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.item.ItemController;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class BookingControllerTest extends ControllerTest {
    @InjectMocks
    private BookingController controller;
    @Mock
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void createBooking_StatusCreated() {
        ResultActions result = performBookingPost(buildCreateBookingRequest());
        expectStatusCreated(result);
    }

    private CreateBookingRequest buildCreateBookingRequest() {
        LocalDateTime now = LocalDateTime.now();
        return CreateBookingRequest.builder()
                .itemId(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
    }

    private ResultActions performBookingPost(CreateBookingRequest body) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.post(BookingController.URL_BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(body))
                    .header(ItemController.HEADER_SHARER, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void approveBooking_StatusOK() {
        ResultActions result = performApproveBooking();
        expectStatusOk(result);
    }

    private ResultActions performApproveBooking() {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.patch(createApproveBookingUrl())
                    .header(ItemController.HEADER_SHARER, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createApproveBookingUrl() {
        return String.format("%s/1?%s=true",
                BookingController.URL_BASE, BookingController.PARAM_APPROVED);
    }

    @Test
    public void getBooking_StatusOK() {
        ResultActions result = performGetBooking();
        expectStatusOk(result);
    }

    private ResultActions performGetBooking() {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(createGetBookingUrl())
                    .header(ItemController.HEADER_SHARER, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createGetBookingUrl() {
        return String.format("%s/1",
                BookingController.URL_BASE);
    }

    @Test
    public void getAllUserBookings_StatusOK() {
        ResultActions result = performGetAllUserBookings();
        expectStatusOk(result);
    }

    private ResultActions performGetAllUserBookings() {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(BookingController.URL_BASE)
                    .header(ItemController.HEADER_SHARER, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllUserItemBookings_StatusOK() {
        ResultActions result = performGetAllUserItemBookings();
        expectStatusOk(result);
    }

    private ResultActions performGetAllUserItemBookings() {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(createGetAllUserItemBookingsUrl())
                    .header(ItemController.HEADER_SHARER, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createGetAllUserItemBookingsUrl() {
        return String.format("%s%s",
                BookingController.URL_BASE, BookingController.URL_OWNER);
    }
}
