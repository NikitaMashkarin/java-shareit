package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Creating item request {}, userId={}", itemRequestCreateDto, userId);
        return itemRequestClient.createRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item requests by userId={}", userId);
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        log.info("Get all item requests");
        return itemRequestClient.getAllItemRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId) {
        log.info("Get item requests with id={}", requestId);
        return itemRequestClient.getItemRequestById(requestId);
    }
}