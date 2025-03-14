package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingRequest;

import java.util.List;

public interface BookingService {

    List<Booking> getBookingByOwner(Long ownerId, BookingState state);

    List<Booking> getBookingByBooker(Long bookerId, BookingState state);

    Booking getBookingById(Long userId, Long id);

    Booking createBooking(Long userId, BookingRequest request);

    Booking updateBookingStatus(Long userId, Long bookingId, BookingStatus status);
}
