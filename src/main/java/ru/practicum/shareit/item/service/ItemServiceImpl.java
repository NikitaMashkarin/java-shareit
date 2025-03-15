package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemWithComment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemWithComment;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item createItem(User user, ItemDto itemDto) {

        Item item = ItemMapper.dtoToItem(user, itemDto);

        userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(User user, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));

        if (!existingItem.getOwner().equals(user)) {
            throw new NotFoundException("Пользователь не является владельцем вещи!");
        }

        Item item = ItemMapper.dtoToItem(user, itemDto);

        item.setId(itemId);

        itemRepository.patchItem(item.getName(), item.getDescription(), item.getAvailable(), item.getId());

        return item;
    }

    @Override
    public Collection<ItemWithComment> getAllItemsByUserId(User user) {
        Collection<ItemWithComment> result = new HashSet<>();
        for (Item item : itemRepository.findAllByOwnerId(user.getId())) {
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            Timestamp lastBooking = bookingRepository.findLastBookingByItem(item.getId());
            Timestamp nextBooking = bookingRepository.findNextBookingByItem(item.getId());
            result.add(toItemWithComment(item, comments, lastBooking, nextBooking));
        }
        return result;
    }

    @Override
    public ItemWithComment getItemById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        Timestamp lastBooking = bookingRepository.findLastBookingByItem(item.getId());
        Timestamp nextBooking = bookingRepository.findNextBookingByItem(item.getId());
        return toItemWithComment(item, comments, lastBooking, nextBooking);
    }

    @Override
    public Collection<Item> searchItem(String searchString) {
        if (searchString.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchString);
    }

    @Override
    public CommentDto makeComment(CommentRequest request, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!"));

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        if (!bookingRepository.existsByItemAndBookerAndEndBeforeAndStatus(item, user, now, BookingStatus.APPROVED)) {
            throw new ValidationException("Пользователь не завершил аренду данной вещи!");
        }

        if (commentRepository.existsByItemAndUser(item, user)) {
            throw new ValidationException("Нельзя оставить отзыв!");
        }

        Comment comment = commentRepository.save(new Comment(null, request.getText(), item, user, now));

        return toCommentDto(comment);
    }
}