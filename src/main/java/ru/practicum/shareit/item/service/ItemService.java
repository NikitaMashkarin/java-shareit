package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithComment;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto getById(Long id);

    List<ItemDto> getAll();

    List<ItemDto> getAllByUser(Long userId);

    List<ItemDto> getAllAvailableByText(String text);

    ItemDto create(Long userId, ItemDto itemCreateRequestDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemUpdateRequestDto);

    void deleteById(Long id);

    CommentDto addComment(Long userId, Long itemId, ItemWithComment requestDto);
}