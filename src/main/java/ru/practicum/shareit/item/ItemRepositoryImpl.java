package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.ValidationException;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    Map<Long, Item> itemsMap = new HashMap<>();

    public ItemDto addItem(ItemDto itemDtoRequest, Long userId) {
        Optional<User> user = userRepository.getUser(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with the given ID was not found.");
        }

        Item item = itemMapper.mapToItem(itemDtoRequest);
        item.setOwner(user.get());
        item.setId(getNextId());
        itemsMap.put(item.getId(), item);

        return itemMapper.mapToItemDto(item);
    }

    public ItemDto updateItem(Long itemId, ItemUpdateDto itemDtoRequest, Long userId) {
        if (!itemsMap.containsKey(itemId)) {
            throw new NotFoundException("Item with the given ID was not found.");
        }

        Item item = itemsMap.get(itemId);

        if (item.getOwner() == null) {
            throw new ValidationException("Владелец вещи не указан");
        }

        Long itemsOwnerId = item.getOwner().getId();
        if (!Objects.equals(itemsOwnerId, userId)) {
            throw new AccessDeniedException("Only the owner can modify the item.");
        }

        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName(itemDtoRequest.getName());
        updatedItem.setDescription(itemDtoRequest.getDescription());
        updatedItem.setAvailable(itemDtoRequest.getAvailable());
        updatedItem.setOwner(item.getOwner());

        if (itemDtoRequest.getAvailable() != null) {
            item.setAvailable(itemDtoRequest.getAvailable());
        }

        itemsMap.put(itemId, updatedItem);

        return itemMapper.mapToItemDto(updatedItem);
    }

    public ItemDto getItem(Long itemId) {
        if (!itemsMap.containsKey(itemId)) {
            throw new NotFoundException("Item with the given ID was not found.");
        }

        return itemMapper.mapToItemDto(itemsMap.get(itemId));
    }

    public List<ItemDto> getOwnerItems(Long ownerId) {
        Collection<Item> allItems = itemsMap.values();
        List<ItemDto> ownerItemsList = new ArrayList<>();

        allItems.forEach(item -> {
            User owner = item.getOwner();
            if (owner != null && Objects.equals(owner.getId(), ownerId)) {
                ownerItemsList.add(itemMapper.mapToItemDto(item));
            }
        });

        if (ownerItemsList.isEmpty()) {
            throw new NotFoundException("User's items were not found.");
        }

        return ownerItemsList;
    }

    public List<ItemDto> itemSearch(String text) {
        List<ItemDto> searchItems = new ArrayList<>();
        if (text != null && !text.isBlank()) {
            Collection<Item> allItems = itemsMap.values();

            allItems.forEach(item -> {
                if (item.getName() != null && Boolean.TRUE.equals(item.getAvailable()) &&
                        item.getName().toLowerCase().contains(text.toLowerCase())) {
                    searchItems.add(itemMapper.mapToItemDto(item));
                }

                if (item.getDescription() != null && Boolean.TRUE.equals(item.getAvailable()) &&
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    searchItems.add(itemMapper.mapToItemDto(item));
                }
            });
        }
        return searchItems;
    }

    public Long getNextId() {
        return itemsMap.values().stream()
                .filter(Objects::nonNull)
                .mapToLong(Item::getId)
                .max()
                .orElse(0) + 1;
    }
}
