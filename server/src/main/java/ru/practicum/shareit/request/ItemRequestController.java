package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.create(userId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests() {
        return itemRequestService.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId) {
        return itemRequestService.getById(requestId);
    }
}