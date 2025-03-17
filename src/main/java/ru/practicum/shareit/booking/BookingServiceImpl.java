package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id = " + bookingId));
        if (!bookingEntity.getItem().getOwner().getId().equals(userId) &&
                !bookingEntity.getBooker().getId().equals(userId)) {
            throw new ForbiddenException("Only item owner and item booker are allowed to view this booking");
        }
        log.debug("Get booking by id = {}", bookingId);
        return bookingMapper.toBookingDto(bookingEntity);
    }

    @Override
    public List<BookingDto> getAllByState(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
        LocalDateTime currentDate = LocalDateTime.now();
        List<BookingEntity> bookingEntityList = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case PAST -> bookingRepository.findAllByBookerIdAndPastOrderByStartDesc(userId, currentDate);
            case CURRENT -> bookingRepository.findAllByBookerIdAndCurrentOrderByStartDesc(userId, currentDate);
            case FUTURE -> bookingRepository.findAllByBookerIdAndFutureOrderByStartDesc(userId, currentDate);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        log.debug("Get all bookings by state = {}. Size = {}", state.name(), bookingEntityList.size());
        return bookingEntityList
                .stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllByOwnerAndState(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
        LocalDateTime currentDate = LocalDateTime.now();
        List<BookingEntity> bookingEntityList = switch (state) {
            case ALL -> bookingRepository.findAllByOwnerIdOrderByStartDesc(userId);
            case PAST -> bookingRepository.findAllByOwnerIdAndPastOrderByStartDesc(userId, currentDate);
            case CURRENT -> bookingRepository.findAllByOwnerIdAndCurrentOrderByStartDesc(userId, currentDate);
            case FUTURE -> bookingRepository.findAllByOwnerIdAndFutureOrderByStartDesc(userId, currentDate);
            case WAITING -> bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        log.debug("Get all bookings by owner and state = {}. Size = {}", state.name(), bookingEntityList.size());
        return bookingEntityList
                .stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public BookingDto create(Long userId, BookingCreateRequestDto bookingCreateRequestDto) {
        ItemEntity itemEntity = itemRepository.findById(bookingCreateRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + bookingCreateRequestDto.getItemId()));

        BookingEntity bookingEntity = bookingMapper.toBookingEntity(bookingCreateRequestDto);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId)));

        if (!itemEntity.getAvailable()) {
            throw new ValidationException("Item is unavailable for booking");
        }
        if (bookingCreateRequestDto.getStart().equals(bookingCreateRequestDto.getEnd()) ||
                bookingCreateRequestDto.getStart().isAfter(bookingCreateRequestDto.getEnd())) {
            throw new ValidationException("Booking start date must be earlier than end date");
        }

        bookingEntity.setStart(bookingCreateRequestDto.getStart());
        bookingEntity.setEnd(bookingCreateRequestDto.getEnd());
        bookingEntity.setStatus(BookingStatus.WAITING);

        BookingEntity createdBookingEntity = bookingRepository.save(bookingEntity);
        log.debug("Booking created with id = {}", createdBookingEntity.getId());
        return bookingMapper.toBookingDto(createdBookingEntity);
    }

    @Override
    public BookingDto setApproval(Long userId, Long bookingId, Boolean approved) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id = " + bookingId));
        if (!bookingEntity.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You can only approve or disapprove bookings for your items");
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        bookingEntity.setStatus(bookingStatus);

        BookingEntity updatedBookingEntity = bookingRepository.save(bookingEntity);
        log.debug("Status {} was set for booking with id = {}",
                bookingStatus.name(),
                updatedBookingEntity.getId());
        return bookingMapper.toBookingDto(updatedBookingEntity);
    }
}