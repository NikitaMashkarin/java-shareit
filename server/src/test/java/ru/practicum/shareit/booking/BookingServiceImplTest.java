package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;

    private ItemDto itemDto1;
    private ItemDto itemDto2;

    private BookingDto bookingDto1;

    @BeforeEach
    void setUp() {
        userDto1 = userService.create(prepareUserCreateRequestDto("name1", "email1@gmail.com"));
        userDto2 = userService.create(prepareUserCreateRequestDto("name2", "email2@gmail.com"));
        userDto3 = userService.create(prepareUserCreateRequestDto("name3", "email3@gmail.com"));

        itemDto1 = itemService.create(userDto1.getId(),
                prepareItemCreateRequestDto("name1", "desc1", true, null));
        itemDto2 = itemService.create(userDto2.getId(),
                prepareItemCreateRequestDto("name2", "desc2", false, null));

        BookingCreateRequestDto bookingCreateRequestDto1 = prepareBookingCreateRequestDto(null,
                LocalDateTime.now().minusHours(4), LocalDateTime.now().minusHours(3),
                itemDto1.getId(), userDto1, BookingStatus.APPROVED);
        BookingCreateRequestDto bookingCreateRequestDto2 = prepareBookingCreateRequestDto(null,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                itemDto1.getId(), userDto1, BookingStatus.WAITING);

        bookingDto1 = bookingService.create(userDto1.getId(), bookingCreateRequestDto1);
        bookingService.create(userDto2.getId(), bookingCreateRequestDto2);
    }

    @Test
    void getById() {
        BookingDto bookingDto = bookingService.getById(bookingDto1.getBooker().getId(), bookingDto1.getId());
        TypedQuery<BookingEntity> query = em.createQuery("Select b from BookingEntity b where b.id = :id",
                BookingEntity.class);
        BookingEntity bookingEntity = query.setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertNotNull(bookingDto);
        assertThat(bookingEntity.getId(), notNullValue());
        assertThat(bookingEntity.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(bookingEntity.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingEntity.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingEntity.getBooker().getId(), equalTo(bookingDto.getBooker().getId()));
        assertThat(bookingEntity.getStatus(), equalTo(bookingDto.getStatus()));

        assertThrows(NotFoundException.class, () -> itemService.getById(Long.MAX_VALUE));
        assertDoesNotThrow(() ->
                bookingService.getById(bookingDto1.getItem().getOwner().getId(), bookingDto1.getId()));
        assertThrows(ForbiddenException.class,
                () -> bookingService.getById(userDto3.getId(), bookingDto1.getId()));
    }

    @Test
    void getAllByState() {
        assertEquals(1, bookingService.getAllByState(userDto1.getId(), BookingState.PAST).size());
        assertEquals(1, bookingService.getAllByState(userDto1.getId(), BookingState.ALL).size());
        assertEquals(0, bookingService.getAllByState(userDto1.getId(), BookingState.CURRENT).size());
        assertEquals(0, bookingService.getAllByState(userDto1.getId(), BookingState.REJECTED).size());
        assertEquals(0, bookingService.getAllByState(userDto1.getId(), BookingState.FUTURE).size());
        assertEquals(1, bookingService.getAllByState(userDto1.getId(), BookingState.WAITING).size());
    }

    @Test
    void getAllByOwnerAndState() {
        assertEquals(2, bookingService.getAllByOwnerAndState(userDto1.getId(), BookingState.PAST).size());
        assertEquals(0, bookingService.getAllByOwnerAndState(userDto2.getId(), BookingState.PAST).size());
        assertEquals(2, bookingService.getAllByOwnerAndState(userDto1.getId(), BookingState.ALL).size());
        assertEquals(0, bookingService.getAllByOwnerAndState(userDto1.getId(), BookingState.CURRENT).size());
        assertEquals(0, bookingService.getAllByOwnerAndState(userDto1.getId(), BookingState.REJECTED).size());
        assertEquals(0, bookingService.getAllByOwnerAndState(userDto1.getId(), BookingState.FUTURE).size());
        assertEquals(2, bookingService.getAllByOwnerAndState(userDto1.getId(), BookingState.WAITING).size());
    }

    @Test
    void create() {
        BookingCreateRequestDto bookingCreateRequestDto = prepareBookingCreateRequestDto(null,
                LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(8),
                itemDto2.getId(), userDto2, BookingStatus.APPROVED);

        assertThrows(ValidationException.class, () -> bookingService.create(userDto2.getId(), bookingCreateRequestDto));
        bookingCreateRequestDto.setItemId(itemDto1.getId());

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingCreateRequestDto);
        TypedQuery<BookingEntity> query = em.createQuery("Select b from BookingEntity b where b.id = :id",
                BookingEntity.class);
        BookingEntity bookingEntity = query.setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertThat(bookingEntity.getId(), notNullValue());
        assertThat(bookingEntity.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(bookingEntity.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingEntity.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingEntity.getBooker().getId(), equalTo(bookingDto.getBooker().getId()));
        assertThat(bookingEntity.getStatus(), equalTo(bookingDto.getStatus()));

        assertThrows(NotFoundException.class, () -> bookingService.create(Long.MAX_VALUE, bookingCreateRequestDto));

        BookingCreateRequestDto bookingCreateRequestDto2 = prepareBookingCreateRequestDto(null,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(2),
                itemDto1.getId(), userDto2, BookingStatus.APPROVED);

        assertThrows(ValidationException.class, () ->
                bookingService.create(userDto2.getId(), bookingCreateRequestDto2));
    }

    @Test
    void setApproval() {
        BookingCreateRequestDto bookingCreateRequestDto = prepareBookingCreateRequestDto(null,
                LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(8),
                itemDto1.getId(), userDto2, BookingStatus.APPROVED);
        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingCreateRequestDto);

        assertThrows(ForbiddenException.class, () ->
                bookingService.setApproval(userDto2.getId(), bookingDto.getId(), false));
        bookingService.setApproval(userDto1.getId(), bookingDto.getId(), false);
        assertThat(BookingStatus.REJECTED,
                equalTo(bookingService.getById(userDto2.getId(), bookingDto.getId()).getStatus()));
    }

    private BookingCreateRequestDto prepareBookingCreateRequestDto(Long id,
                                                                   LocalDateTime start,
                                                                   LocalDateTime end,
                                                                   Long itemId,
                                                                   UserDto booker,
                                                                   BookingStatus status) {
        return new BookingCreateRequestDto(id, start, end, itemId, booker, status);
    }

    private ItemCreateRequestDto prepareItemCreateRequestDto(String name, String description,
                                                             Boolean available, Long requestId) {
        return new ItemCreateRequestDto(name, description, available, requestId);
    }

    private UserCreateRequestDto prepareUserCreateRequestDto(String name, String email) {
        return new UserCreateRequestDto(name, email);
    }
}