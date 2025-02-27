package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto item) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId, @RequestBody ItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestParam String text) {
        return itemService.itemSearch(text);
    }
}
