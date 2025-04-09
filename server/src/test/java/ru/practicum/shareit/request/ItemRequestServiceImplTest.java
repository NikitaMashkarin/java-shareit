package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private UserDto userDto1;
    private UserDto userDto2;

    private ItemRequestDto itemRequestDto1;

    @BeforeEach
    void setUp() {
        userDto1 = userService.create(prepareUserCreateRequestDto("name1", "email1@gmail.com"));
        userDto2 = userService.create(prepareUserCreateRequestDto("name2", "email2@gmail.com"));

        itemRequestDto1 = itemRequestService.create(userDto1.getId(), prepareItemRequestCreateDto("desc1"));
        itemRequestService.create(userDto2.getId(), prepareItemRequestCreateDto("desc2"));
    }

    @Test
    void create() {
        ItemRequestCreateDto itemRequestCreateDto = prepareItemRequestCreateDto("333");
        ItemRequestDto itemRequestDto = itemRequestService.create(userDto2.getId(), itemRequestCreateDto);
        TypedQuery<ItemRequestEntity> query = em.createQuery("Select ir from ItemRequestEntity ir where ir.id = :id",
                ItemRequestEntity.class);
        ItemRequestEntity itemRequestEntity = query.setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        assertNotNull(itemRequestDto);
        assertThat(itemRequestEntity.getId(), notNullValue());
        assertThat(itemRequestEntity.getDescription(), equalTo(itemRequestCreateDto.getDescription()));
        assertThat(itemRequestEntity.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequestEntity.getRequestor().getId(), equalTo(itemRequestDto.getRequestor().getId()));

        assertThrows(NotFoundException.class, () -> itemRequestService.create(Long.MAX_VALUE, itemRequestCreateDto));
    }

    @Test
    void getAllByRequestorId() {
        TypedQuery<Long> query = em.createQuery("Select count(*) from ItemRequestEntity ir " +
                "inner join ir.requestor u " +
                "where u.id = :userId", Long.class);
        Long count = query.setParameter("userId", userDto1.getId()).getSingleResult();
        assertEquals(count, itemRequestService.getAllByRequestorId(userDto1.getId()).size());
    }

    @Test
    void getAll() {
        TypedQuery<Long> query = em.createQuery("Select count(*) from ItemRequestEntity ir", Long.class);
        Long count = query.getSingleResult();
        assertEquals(count, itemRequestService.getAll().size());
    }

    @Test
    void getById() {
        ItemRequestDto itemRequestDto = itemRequestService.getById(itemRequestDto1.getId());
        TypedQuery<ItemRequestEntity> query = em.createQuery("Select ir from ItemRequestEntity ir where ir.id = :id",
                ItemRequestEntity.class);
        ItemRequestEntity itemRequestEntity = query.setParameter("id", itemRequestDto1.getId())
                .getSingleResult();

        assertNotNull(itemRequestDto);
        assertThat(itemRequestEntity.getId(), notNullValue());
        assertThat(itemRequestEntity.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestEntity.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequestEntity.getRequestor().getId(), equalTo(itemRequestDto.getRequestor().getId()));

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(Long.MAX_VALUE));
    }

    private ItemRequestCreateDto prepareItemRequestCreateDto(String description) {
        return new ItemRequestCreateDto(description);
    }

    private UserCreateRequestDto prepareUserCreateRequestDto(String name, String email) {
        return new UserCreateRequestDto(name, email);
    }
}