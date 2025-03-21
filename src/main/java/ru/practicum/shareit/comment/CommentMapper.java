package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.ItemCommentDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorName",
            expression = "java(commentEntity.getAuthor() != null ? commentEntity.getAuthor().getName() : null)")
    CommentDto toCommentDto(CommentEntity commentEntity);

    @Mapping(target = "authorName",
            expression = "java(commentEntity.getAuthor() != null ? commentEntity.getAuthor().getName() : null)")
    ItemCommentDto toItemCommentDto(CommentEntity commentEntity);
}