package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithComment;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorName",
            expression = "java(commentEntity.getAuthor() != null ? commentEntity.getAuthor().getName() : null)")
    CommentDto toCommentDto(Comment commentEntity);

    @Mapping(target = "authorName",
            expression = "java(commentEntity.getAuthor() != null ? commentEntity.getAuthor().getName() : null)")
    ItemWithComment toItemCommentDto(Comment commentEntity);
}
