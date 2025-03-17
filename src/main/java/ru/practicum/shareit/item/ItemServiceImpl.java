package ru.practicum.shareit.item;

import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentEntity;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentItemRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto getById(Long id) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + id));
        log.debug("Get item with id = {}", id);
        return itemMapper.toItemDto(itemEntity, commentRepository.findAllByItemId(id));
    }

    @Override
    public List<ItemDto> getAll() {
        log.debug("Get all items");
        return itemRepository.findAll()
                .stream()
                .map(itemEntity -> itemMapper.toItemDto(itemEntity, commentRepository.findAllByItemId(itemEntity.getId())))
                .toList();
    }

    @Override
    public List<ItemDto> getAllByUser(Long userId) {
        log.debug("Get all items by user = {}", userId);
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getAllAvailableByText(String text) {
        if (StringUtil.isNullOrEmpty(text)) {
            return List.of();
        }

        log.debug("Get all items by text = {}", text);
        return itemRepository.findByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto create(Long userId, ItemCreateRequestDto itemCreateRequestDto) {
        ItemEntity itemEntity = itemMapper.toItemEntity(itemCreateRequestDto);
        itemEntity.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId)));
        ItemEntity createdItemEntity = itemRepository.save(itemEntity);
        log.debug("Item created with id = {}", createdItemEntity.getId());
        return itemMapper.toItemDto(createdItemEntity);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateRequestDto itemUpdateRequestDto) {
        ItemEntity itemEntity = itemMapper.toItemEntity(itemUpdateRequestDto);
        ItemEntity itemEntityForUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + itemId));

        if (!itemEntityForUpdate.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You can only edit your items");
        }

        itemEntityForUpdate.setName(!StringUtil.isNullOrEmpty(itemEntity.getName()) ?
                itemEntity.getName() : itemEntityForUpdate.getName());
        itemEntityForUpdate.setDescription(!StringUtil.isNullOrEmpty(itemEntity.getDescription()) ?
                itemEntity.getDescription() : itemEntityForUpdate.getDescription());
        itemEntityForUpdate.setAvailable(itemEntity.getAvailable() != null ?
                itemEntity.getAvailable() : itemEntityForUpdate.getAvailable());

        ItemEntity updatedItemEntity = itemRepository.save(itemEntityForUpdate);
        log.debug("Item was updated with id = {}", itemId);
        return itemMapper.toItemDto(updatedItemEntity);
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
        log.debug("Item with id = {} was deleted", id);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentItemRequestDto requestDto) {
        CommentEntity commentEntity = new CommentEntity();
        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + itemId));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
        LocalDateTime currentDate = LocalDateTime.now();

        boolean hasNotBookedThisItem =
                bookingRepository.findAllByBookerIdAndPastOrderByStartDesc(userId, currentDate).isEmpty();
        if (hasNotBookedThisItem) {
            throw new ValidationException("You can not post comment for this item, " +
                    "because you haven't booked it before");
        }

        commentEntity.setText(requestDto.getText());
        commentEntity.setItem(itemEntity);
        commentEntity.setAuthor(userEntity);
        commentEntity.setCreated(LocalDateTime.now());

        CommentEntity createdCommentEntity = commentRepository.save(commentEntity);
        log.debug("Comment with id = {} was added to item with id = {}", createdCommentEntity.getId(), itemId);
        return commentMapper.toCommentDto(createdCommentEntity);
    }
}