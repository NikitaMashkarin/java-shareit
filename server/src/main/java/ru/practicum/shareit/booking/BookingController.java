package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false) BookingState state) {
        state = state == null ? BookingState.ALL : state;
        return bookingService.getAllByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false) BookingState state) {
        state = state == null ? BookingState.ALL : state;
        return bookingService.getAllByOwnerAndState(userId, state);
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingCreateRequestDto bookingCreateRequestDto) {
        return bookingService.create(userId, bookingCreateRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingApproval(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        return bookingService.setApproval(userId, bookingId, approved);
    }
}