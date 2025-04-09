package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByState(Long userId, BookingState state);

    List<BookingDto> getAllByOwnerAndState(Long userId, BookingState state);

    BookingDto create(Long userId, BookingCreateRequestDto bookingCreateRequestDto);

    BookingDto setApproval(Long userId, Long bookingId, Boolean approved);
}