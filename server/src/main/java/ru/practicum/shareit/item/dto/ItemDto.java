package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.ItemCommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<ItemCommentDto> comments;
    private Long request;
}