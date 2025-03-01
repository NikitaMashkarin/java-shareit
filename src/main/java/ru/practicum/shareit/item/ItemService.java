package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDtoRequest, Long userId);

    ItemDto updateItem(Long itemId, ItemUpdateDto itemDtoRequest, Long userId);

    ItemDto getItem(Long itemId);

    List<ItemDto> getOwnerItems(Long ownerId);

    List<ItemDto> itemSearch(String text);
}
