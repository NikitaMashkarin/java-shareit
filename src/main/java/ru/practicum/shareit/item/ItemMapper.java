package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.comment.CommentEntity;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CommentMapper.class})
public interface ItemMapper {

    @Mapping(target = "request",
            expression = "java(itemEntity.getRequest() != null ? itemEntity.getRequest().getId() : null)")
    ItemDto toItemDto(ItemEntity itemEntity);

    @Mapping(target = "request",
            expression = "java(itemEntity.getRequest() != null ? itemEntity.getRequest().getId() : null)")
    ItemDto toItemDto(ItemEntity itemEntity, List<CommentEntity> comments);

    @Mapping(target = "request", ignore = true)
    ItemEntity toItemEntity(ItemDto itemDto);

    ItemEntity toItemEntity(ItemCreateRequestDto itemCreateRequestDto);

    ItemEntity toItemEntity(ItemUpdateRequestDto itemUpdateRequestDto);
}