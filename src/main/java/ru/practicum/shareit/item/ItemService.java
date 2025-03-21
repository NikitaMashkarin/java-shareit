package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentItemRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto getById(Long id);

    List<ItemDto> getAll();

    List<ItemDto> getAllByUser(Long userId);

    List<ItemDto> getAllAvailableByText(String text);

    ItemDto create(Long userId, ItemCreateRequestDto itemCreateRequestDto);

    ItemDto update(Long userId, Long itemId, ItemUpdateRequestDto itemUpdateRequestDto);

    void deleteById(Long id);

    CommentDto addComment(Long userId, Long itemId, CommentItemRequestDto requestDto);
}