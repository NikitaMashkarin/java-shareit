package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(ItemDto itemDtoRequest, Long userId) {
        return itemRepository.addItem(itemDtoRequest, userId);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto itemDtoRequest, Long userId) {
        return itemRepository.updateItem(itemId, itemDtoRequest, userId);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        return itemRepository.getOwnerItems(ownerId);
    }

    @Override
    public List<ItemDto> itemSearch(String text) {
        return itemRepository.itemSearch(text);
    }
}
