package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ItemMapper.class})
public interface BookingMapper {

    BookingRequest toBookingRequestDto(Booking bookingEntity);

    Booking toBookingEntity(BookingRequest bookingCreateRequestDto);

    BookingDto toBookingDto(Booking bookingEntity);

    Booking toBookingEntity(BookingDto bookingDto);
}