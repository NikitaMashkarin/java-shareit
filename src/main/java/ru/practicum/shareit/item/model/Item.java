package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Booking available;
    private User owner;
    private ItemRequest request;

    public StatusBooking isAvailable(){
        return available.getStatus();
    }
}
