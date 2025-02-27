package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.StatusBooking;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private StatusBooking available;
    private Long request;
}
