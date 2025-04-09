package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllUserBookings(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByOwner(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("/owner?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> addBooking(Long userId, BookingCreateRequestDto bookingCreateRequestDto) {
        return post("", userId, bookingCreateRequestDto);
    }

    public ResponseEntity<Object> setBookingApproval(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved.toString()
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }
}