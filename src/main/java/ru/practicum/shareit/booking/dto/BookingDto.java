package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@AllArgsConstructor
public class BookingDto {
    @FutureOrPresent
    Timestamp start;
    @Future
    Timestamp end;
    BookingStatus status;
    Long bookerId;
    Long itemId;
    String itemName;
}
