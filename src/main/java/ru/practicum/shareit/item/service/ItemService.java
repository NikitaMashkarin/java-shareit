package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithComment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto getById(Long id);

    List<ItemDto> getAll();

    List<ItemDto> getAllByUser(Long userId);

    List<ItemDto> getAllAvailableByText(String text);

    ItemDto create(Long userId, Item itemCreateRequestDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemUpdateRequestDto);

    CommentDto addComment(Long userId, Long itemId, ItemWithComment requestDto);
}