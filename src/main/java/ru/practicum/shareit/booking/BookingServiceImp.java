package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Booking> getBookingByBooker(Long bookerId, BookingState state) {
        Timestamp now = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        if (state == null) {
            state = BookingState.ALL;
        }
        return switch (state) {
            case CURRENT ->
                    bookingRepository.findBookingsByBookerIdAndStartAfterAndEndBeforeOrderByIdDesc(bookerId, now, now);
            case PAST -> bookingRepository.findBookingsByBookerIdAndEndAfterOrderByIdDesc(bookerId, now);
            case FUTURE -> bookingRepository.findBookingsByBookerIdAndStartAfterOrderByIdDesc(bookerId, now);
            case WAITING, REJECTED -> bookingRepository.findBookingsByBookerIdAndStatusOrderByIdDesc(bookerId,
                    BookingStatus.valueOf(state.toString()));
            default -> bookingRepository.findBookingsByBookerIdOrderByIdDesc(bookerId);
        };
    }

    @Override
    public List<Booking> getBookingByOwner(Long ownerId, BookingState state) {
        Timestamp now = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        if (state == null) {
            state = BookingState.ALL;
        }

        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        return switch (state) {
            case CURRENT ->
                    bookingRepository.findBookingsByItemOwnerIdAndStartAfterAndEndBeforeOrderByIdDesc(ownerId, now, now);
            case PAST -> bookingRepository.findBookingsByItemOwnerIdAndEndAfterOrderByIdDesc(ownerId, now);
            case FUTURE -> bookingRepository.findBookingsByItemOwnerIdAndStartAfterOrderByIdDesc(ownerId, now);
            case WAITING, REJECTED -> bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByIdDesc(ownerId,
                    BookingStatus.valueOf(state.toString()));
            default -> bookingRepository.findBookingsByItemOwnerIdOrderByIdDesc(ownerId);
        };
    }

    @Override
    public Booking getBookingById(Long userId, Long id) {
        return bookingRepository.findBookingByBookerIdAndId(userId, id).orElseThrow(()
                -> new NotFoundException("Бронирование найдено!"));
    }

    @Override
    public Booking createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования!");
        }

        Timestamp now = Timestamp.valueOf(LocalDate.now().atStartOfDay());

        if (request.getEnd().before(now)) {
            throw new ValidationException("Дата окончания не может быть меньше текущей!");
        }

        if (request.getStart().before(now)) {
            throw new ValidationException("Дата начала не может быть меньше текущей!");
        }

        if (request.getStart() == request.getEnd()) {
            throw new ValidationException("Дата начала не должна совпадать с датой окончания!");
        }

        return bookingRepository.save(new Booking(null, request.getStart(), request.getEnd(), item, user, BookingStatus.WAITING));
    }

    @Override
    public Booking updateBookingStatus(Long userId, Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Бронирование не найдено!"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Неверный пользователь!");
        }

        booking.setStatus(status);

        bookingRepository.save(booking);

        return booking;
    }
}
