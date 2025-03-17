package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ItemMapper.class})
public interface BookingMapper {

    BookingCreateRequestDto toBookingRequestDto(BookingEntity bookingEntity);

    BookingEntity toBookingEntity(BookingCreateRequestDto bookingCreateRequestDto);

    BookingDto toBookingDto(BookingEntity bookingEntity);

    BookingEntity toBookingEntity(BookingDto bookingDto);
}