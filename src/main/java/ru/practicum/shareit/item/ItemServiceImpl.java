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
    public ItemDto addItem(ItemDto itemDtoRequest, Integer userId) {
        return itemRepository.addItem(itemDtoRequest, userId);
    }

    @Override
    public ItemDto updateItem(Integer itemId, ItemUpdateDto itemDtoRequest, Integer userId) {
        return itemRepository.updateItem(itemId, itemDtoRequest, userId);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getOwnerItems(Integer ownerId) {
        return itemRepository.getOwnerItems(ownerId);
    }

    @Override
    public List<ItemDto> itemSearch(String text) {
        return itemRepository.itemSearch(text);
    }
}
