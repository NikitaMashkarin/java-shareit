package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.comment.dto.CommentItemRequestDto;
import ru.practicum.shareit.comment.dto.ItemCommentDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.ItemEntity;
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
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        userDto1 = userService.create(prepareUserCreateRequestDto("name1", "email1@gmail.com"));
        userDto2 = userService.create(prepareUserCreateRequestDto("name2", "email2@gmail.com"));

        itemService.create(userDto1.getId(),
                prepareItemCreateRequestDto("name1", "desc1", true, null));
        itemService.create(userDto2.getId(),
                prepareItemCreateRequestDto("name2", "desc2", false, null));
        itemService.create(userDto1.getId(),
                prepareItemCreateRequestDto("name3", "desc3", true, null));
    }

    @Test
    void getById() {
        ItemCreateRequestDto itemCreateRequestDto =
                prepareItemCreateRequestDto("name4", "desc4", true, null);
        ItemDto itemDto = itemService.create(userDto1.getId(), itemCreateRequestDto);
        ItemDto itemDto1 = itemService.getById(itemDto.getId());
        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id", ItemEntity.class);
        ItemEntity itemEntity = query.setParameter("id", itemDto.getId())
                .getSingleResult();

        assertNotNull(itemDto1);
        assertThat(itemDto1.getId(), notNullValue());
        assertThat(itemDto1.getName(), equalTo(itemDto.getName()));
        assertThat(itemDto1.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDto1.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemDto1.getRequest(), equalTo(itemDto.getRequest()));

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemDto1.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(itemEntity.getAvailable(), equalTo(itemDto1.getAvailable()));
        assertThat(itemEntity.getRequest(), equalTo(itemDto1.getRequest()));

        assertThrows(NotFoundException.class, () -> itemService.getById(Long.MAX_VALUE));
    }

    @Test
    void getAll() {
        TypedQuery<Long> query = em.createQuery("Select count(*) from ItemEntity i", Long.class);
        Long count = query.getSingleResult();
        assertEquals(count, itemService.getAll().size());
    }

    @Test
    void getAllByUser() {
        Long userId = userDto1.getId();
        TypedQuery<Long> query = em.createQuery("select count(*) from ItemEntity i " +
                "inner join i.owner u " +
                "where u.id = :userId", Long.class);
        Long count = query.setParameter("userId", userId).getSingleResult();
        assertEquals(count, itemService.getAllByUser(userId).size());
    }

    @Test
    void getAllAvailableByText() {
        String textFilter = "name";
        TypedQuery<Long> query = em.createQuery("select count(*) from ItemEntity i " +
                "where (upper(i.name) like upper(concat('%', :text, '%')) " +
                " or upper(i.description) like upper(concat('%', :text, '%'))) " +
                " and i.available", Long.class);
        Long count = query.setParameter("text", textFilter).getSingleResult();
        assertEquals(count, itemService.getAllAvailableByText(textFilter).size());
        assertEquals(0, itemService.getAllAvailableByText(null).size());
    }

    @Test
    void create() {
        ItemCreateRequestDto itemCreateRequestDto =
                prepareItemCreateRequestDto("name5", "desc5", true, null);
        ItemDto itemDto = itemService.create(userDto2.getId(), itemCreateRequestDto);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id", ItemEntity.class);
        ItemEntity itemEntity = query.setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemCreateRequestDto.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemCreateRequestDto.getDescription()));
        assertThat(itemEntity.getAvailable(), equalTo(itemCreateRequestDto.getAvailable()));
        assertThat(itemEntity.getRequest() == null ? null : itemEntity.getRequest().getId(),
                equalTo(itemCreateRequestDto.getRequestId()));

        assertThrows(NotFoundException.class, () -> itemService.create(Long.MAX_VALUE, itemCreateRequestDto));
        itemCreateRequestDto.setRequestId(Long.MAX_VALUE);
        assertThrows(NotFoundException.class, () -> itemService.create(userDto2.getId(), itemCreateRequestDto));
    }

    @Test
    void update() {
        ItemDto itemDto = itemService.getAll().getFirst();
        Long itemId = itemDto.getId();
        Long ownerId = itemDto.getOwner().getId();
        ItemUpdateRequestDto itemUpdateRequestDto =
                prepareItemUpdateRequestDto("name11", "desc11", true);
        itemService.update(ownerId, itemId, itemUpdateRequestDto);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id", ItemEntity.class);
        ItemEntity itemEntity = query.setParameter("id", itemId)
                .getSingleResult();

        assertThat(itemEntity.getId(), equalTo(itemId));
        assertThat(itemEntity.getName(), equalTo(itemUpdateRequestDto.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemUpdateRequestDto.getDescription()));
        assertThat(itemEntity.getAvailable(), equalTo(itemUpdateRequestDto.getAvailable()));
        assertThat(itemEntity.getRequest() == null ?
                        null : itemEntity.getRequest().getId(),
                equalTo(itemDto.getRequest() == null ? null : itemDto.getId()));

        assertThrows(NotFoundException.class, () -> itemService.update(ownerId, Long.MAX_VALUE, itemUpdateRequestDto));
        assertThrows(ForbiddenException.class, () -> itemService.update(Long.MAX_VALUE, itemId, itemUpdateRequestDto));

        ItemUpdateRequestDto itemUpdateRequestDto2 =
                prepareItemUpdateRequestDto(null, null, null);
        itemService.update(ownerId, itemId, itemUpdateRequestDto2);
        assertThat(itemEntity.getId(), equalTo(itemId));
        assertThat(itemEntity.getName(), equalTo(itemUpdateRequestDto.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemUpdateRequestDto.getDescription()));
        assertThat(itemEntity.getAvailable(), equalTo(itemUpdateRequestDto.getAvailable()));
        assertThat(itemEntity.getRequest() == null ?
                        null : itemEntity.getRequest().getId(),
                equalTo(itemDto.getRequest() == null ? null : itemDto.getId()));
    }

    @Test
    void deleteById() {
        ItemDto itemDto = itemService.create(userDto1.getId(),
                prepareItemCreateRequestDto("name6", "desc6", true, null));

        itemService.deleteById(itemDto.getId());
        assertThrows(NotFoundException.class, () -> itemService.getById(itemDto.getId()));
    }

    @Test
    void addComment() {
        ItemDto itemDto = itemService.getAll().getFirst();
        CommentItemRequestDto commentItemRequestDto = prepareCommentItemRequestDto("123");
        assertThrows(ValidationException.class, () ->
                itemService.addComment(userDto1.getId(), itemDto.getId(), commentItemRequestDto));

        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        BookingCreateRequestDto bookingCreateRequestDto = prepareBookingCreateRequestDto(null,
                start, end, itemDto.getId(), userDto1, BookingStatus.APPROVED);

        bookingService.create(userDto1.getId(), bookingCreateRequestDto);
        itemService.addComment(userDto1.getId(), itemDto.getId(), commentItemRequestDto);
        ItemCommentDto commentDto = itemService.getById(itemDto.getId()).getComments().getLast();
        assertThat(userDto1.getName(), equalTo(commentDto.getAuthorName()));
        assertThat(commentItemRequestDto.getText(), equalTo(commentDto.getText()));
    }

    private ItemCreateRequestDto prepareItemCreateRequestDto(String name, String description,
                                                             Boolean available, Long requestId) {
        return new ItemCreateRequestDto(name, description, available, requestId);
    }

    private ItemUpdateRequestDto prepareItemUpdateRequestDto(String name, String description,
                                                             Boolean available) {
        return new ItemUpdateRequestDto(name, description, available);
    }

    private UserCreateRequestDto prepareUserCreateRequestDto(String name, String email) {
        return new UserCreateRequestDto(name, email);
    }

    private CommentItemRequestDto prepareCommentItemRequestDto(String text) {
        return new CommentItemRequestDto(text);
    }

    private BookingCreateRequestDto prepareBookingCreateRequestDto(Long id, LocalDateTime start,
                                                                   LocalDateTime end, Long itemId,
                                                                   UserDto booker, BookingStatus status) {
        return new BookingCreateRequestDto(id, start, end, itemId, booker, status);
    }
}