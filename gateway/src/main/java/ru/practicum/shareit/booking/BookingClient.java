package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.client.RestClient;
import ru.practicum.shareit.urlbuilder.UrlBuilder;

@Service
public class BookingClient extends RestClient {
    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BookingController.URL_BASE))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(long sharerId, CreateBookingRequest request) {
        return post(sharerId, request);
    }

    public ResponseEntity<Object> approveBooking(long sharerId, long bookingId, boolean approved) {
        return patch(UrlBuilder.start()
                        .id(bookingId)
                        .param(BookingController.PARAM_APPROVED, approved)
                        .build(),
                sharerId);
    }

    public ResponseEntity<Object> getBooking(long sharerId, Long bookingId) {
        return get(UrlBuilder.start()
                        .id(bookingId)
                        .build(),
                sharerId);
    }

    public ResponseEntity<Object> getAllUserBookings(long sharerId, BookingSearchState state) {
        return get(UrlBuilder.start()
                        .param(BookingController.PARAM_STATE, state)
                        .build(),
                sharerId);
    }

    public ResponseEntity<Object> getAllUserItemBookings(long sharerId, BookingSearchState state) {
        return get(UrlBuilder.start()
                        .pathPart(BookingController.URL_OWNER)
                        .param(BookingController.PARAM_STATE, state)
                        .build(),
                sharerId);
    }
}
