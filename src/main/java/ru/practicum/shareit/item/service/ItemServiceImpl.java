package ru.practicum.shareit.item.service;

import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithComment;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;


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
        Item itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + id));
        return itemMapper.toItemDto(itemEntity, commentRepository.findAllByItemId(id));
    }

    @Override
    public List<ItemDto> getAll() {
        return itemRepository.findAll()
                .stream()
                .map(itemEntity -> itemMapper.toItemDto(itemEntity, commentRepository.findAllByItemId(itemEntity.getId())))
                .toList();
    }

    @Override
    public List<ItemDto> getAllByUser(Long userId) {
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

        return itemRepository.findByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto create(Long userId, Item itemCreateRequestDto) {
        itemCreateRequestDto.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId)));

        Item createdItemEntity = itemRepository.save(itemCreateRequestDto);
        return itemMapper.toItemDto(createdItemEntity);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemUpdateRequestDto) {
        Item itemEntity = itemMapper.toItemEntity(itemUpdateRequestDto);
        Item itemEntityForUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + itemId));

        if (!itemEntityForUpdate.getOwner().getId().equals(userId)) {
            throw new NotFoundException("You can only edit your items");
        }

        itemEntityForUpdate.setName(!StringUtil.isNullOrEmpty(itemEntity.getName()) ?
                itemEntity.getName() : itemEntityForUpdate.getName());
        itemEntityForUpdate.setDescription(!StringUtil.isNullOrEmpty(itemEntity.getDescription()) ?
                itemEntity.getDescription() : itemEntityForUpdate.getDescription());
        itemEntityForUpdate.setAvailable(itemEntity.getAvailable() != null ?
                itemEntity.getAvailable() : itemEntityForUpdate.getAvailable());

        Item updatedItemEntity = itemRepository.save(itemEntityForUpdate);
        return itemMapper.toItemDto(updatedItemEntity);
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, ItemWithComment requestDto) {
        Comment commentEntity = new Comment();
        Item itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + itemId));
        User userEntity = userRepository.findById(userId)
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

        Comment createdCommentEntity = commentRepository.save(commentEntity);
        return commentMapper.toCommentDto(createdCommentEntity);
    }
}