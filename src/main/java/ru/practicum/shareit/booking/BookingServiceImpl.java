package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking bookingEntity = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id = " + bookingId));
        if (!bookingEntity.getItem().getOwner().getId().equals(userId) &&
                !bookingEntity.getBooker().getId().equals(userId)) {
            throw new ValidationException("Only item owner and item booker are allowed to view this booking");
        }
        return bookingMapper.toBookingDto(bookingEntity);
    }

    @Override
    public List<BookingDto> getAllByState(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
        LocalDateTime currentDate = LocalDateTime.now();
        List<Booking> bookingEntityList = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case PAST -> bookingRepository.findAllByBookerIdAndPastOrderByStartDesc(userId, currentDate);
            case CURRENT -> bookingRepository.findAllByBookerIdAndCurrentOrderByStartDesc(userId, currentDate);
            case FUTURE -> bookingRepository.findAllByBookerIdAndFutureOrderByStartDesc(userId, currentDate);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

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
        List<Booking> bookingEntityList = switch (state) {
            case ALL -> bookingRepository.findAllByOwnerIdOrderByStartDesc(userId);
            case PAST -> bookingRepository.findAllByOwnerIdAndPastOrderByStartDesc(userId, currentDate);
            case CURRENT -> bookingRepository.findAllByOwnerIdAndCurrentOrderByStartDesc(userId, currentDate);
            case FUTURE -> bookingRepository.findAllByOwnerIdAndFutureOrderByStartDesc(userId, currentDate);
            case WAITING -> bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookingEntityList
                .stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public BookingDto create(Long userId, BookingRequest bookingCreateRequestDto) {
        Item itemEntity = itemRepository.findById(bookingCreateRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + bookingCreateRequestDto.getItemId()));

        Booking bookingEntity = bookingMapper.toBookingEntity(bookingCreateRequestDto);
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

        Booking createdBookingEntity = bookingRepository.save(bookingEntity);
        return bookingMapper.toBookingDto(createdBookingEntity);
    }

    @Override
    public BookingDto setApproval(Long userId, Long bookingId, Boolean approved) {
        Booking bookingEntity = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id = " + bookingId));
        if (!bookingEntity.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("You can only approve or disapprove bookings for your items");
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        bookingEntity.setStatus(bookingStatus);

        Booking updatedBookingEntity = bookingRepository.save(bookingEntity);
        return bookingMapper.toBookingDto(updatedBookingEntity);
    }
}
