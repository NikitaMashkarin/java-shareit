package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto);

    List<ItemRequestDto> getAllByRequestorId(Long userId);

    List<ItemRequestDto> getAll();

    ItemRequestDto getById(Long id);
}