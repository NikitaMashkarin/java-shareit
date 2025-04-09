package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentItemRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id) {
        log.debug("Get item {}", id);
        return itemClient.getItemById(id);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemCreateRequestDto itemCreateRequestDto) {
        log.info("Creating item {}, userId={}", itemCreateRequestDto, userId);
        return itemClient.addItem(userId, itemCreateRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody ItemUpdateRequestDto itemUpdateRequestDto) {
        log.info("Updating item {}, userId={}", itemUpdateRequestDto, userId);
        return itemClient.updateItem(userId, itemId, itemUpdateRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get items by userId={}", userId);
        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam String text) {
        log.info("Get items by text={}", text);
        return itemClient.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentItemRequestDto commentItemRequestDto) {
        log.info("Creating comment {}, itemId={}, userId={}", commentItemRequestDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentItemRequestDto);
    }
}