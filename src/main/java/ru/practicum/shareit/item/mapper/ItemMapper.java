package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CommentMapper.class})
public interface ItemMapper {

    @Mapping(target = "request",
            expression = "java(itemEntity.getRequest() != null ? itemEntity.getRequest().getId() : null)")
    ItemDto toItemDto(Item itemEntity);

    @Mapping(target = "request",
            expression = "java(itemEntity.getRequest() != null ? itemEntity.getRequest().getId() : null)")
    ItemDto toItemDto(Item itemEntity, List<Comment> comments);

    @Mapping(target = "request", ignore = true)
    Item toItemEntity(ItemDto itemDto);
}