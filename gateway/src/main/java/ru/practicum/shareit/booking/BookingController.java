package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking with id={}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false) BookingState state) {
        state = state == null ? BookingState.ALL : state;
        log.info("Get user bookings by userId={} and state={}", userId, state.name());
        return bookingClient.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(required = false) BookingState state) {
        state = state == null ? BookingState.ALL : state;
        log.info("Get owner bookings by userId={} and state={}", userId, state.name());
        return bookingClient.getAllBookingsByOwner(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody BookingCreateRequestDto bookingCreateRequestDto) {
        log.info("Creating booking {}, userId={}", bookingCreateRequestDto, userId);
        return bookingClient.addBooking(userId, bookingCreateRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setBookingApproval(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam @NotNull Boolean approved) {
        log.info("Setting booking approval for id={}, userId={}, approved={}",
                bookingId, userId, approved);
        return bookingClient.setBookingApproval(userId, bookingId, approved);
    }
}