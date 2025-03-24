package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ItemMapper.class, UserMapper.class})
public interface ItemRequestMapper {

    ItemRequestDto toItemRequestDto(ItemRequestEntity itemRequestEntity, List<ItemEntity> items);

    ItemRequestEntity toItemRequestEntity(ItemRequestCreateDto itemRequestCreateDto);
}