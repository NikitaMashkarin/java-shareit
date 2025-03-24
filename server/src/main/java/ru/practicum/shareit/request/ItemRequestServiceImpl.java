package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        ItemRequestEntity itemRequestEntity = itemRequestMapper.toItemRequestEntity(itemRequestCreateDto);
        itemRequestEntity.setRequestor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId)));
        itemRequestEntity.setCreated(LocalDateTime.now());
        ItemRequestEntity createdItemRequestEntity = itemRequestRepository.save(itemRequestEntity);
        log.debug("Item request created with id = {}", createdItemRequestEntity.getId());
        return itemRequestMapper.toItemRequestDto(createdItemRequestEntity, List.of());
    }

    @Override
    public List<ItemRequestDto> getAllByRequestorId(Long userId) {
        log.debug("Get all item requests by user");
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(request ->
                        itemRequestMapper.toItemRequestDto(request,
                                itemRepository.findAllByRequestId(request.getId())))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll() {
        log.debug("Get all item requests");
        return itemRequestRepository.findAllByOrderByCreatedDesc()
                .stream()
                .map(request ->
                        itemRequestMapper.toItemRequestDto(request,
                                itemRepository.findAllByRequestId(request.getId())))
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long id) {
        log.debug("Get item request with id = {}", id);
        ItemRequestEntity itemRequestEntity = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id = " + id));
        return itemRequestMapper.toItemRequestDto(itemRequestEntity,
                itemRepository.findAllByRequestId(id));
    }
}