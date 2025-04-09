package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService userService;

    @BeforeEach
    void setUp() {
        userService.create(prepareUserCreateRequestDto("name1", "email1@gmail.com"));
        userService.create(prepareUserCreateRequestDto("name2", "email2@gmail.com"));
        userService.create(prepareUserCreateRequestDto("name3", "email3@gmail.com"));
    }

    @Test
    void getById() {
        UserCreateRequestDto userCreateRequestDto =
                prepareUserCreateRequestDto("name4", "email4@gmail.com");
        UserDto userDto = userService.create(userCreateRequestDto);
        UserDto userDto1 = userService.getById(userDto.getId());
        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.id = :id", UserEntity.class);
        UserEntity userEntity = query.setParameter("id", userDto.getId())
                .getSingleResult();

        assertNotNull(userDto1);
        assertThat(userDto1.getId(), notNullValue());
        assertThat(userDto1.getName(), equalTo(userDto.getName()));
        assertThat(userDto1.getEmail(), equalTo(userDto.getEmail()));

        assertThat(userEntity.getId(), notNullValue());
        assertThat(userEntity.getName(), equalTo(userDto1.getName()));
        assertThat(userEntity.getEmail(), equalTo(userDto1.getEmail()));

        assertThrows(NotFoundException.class, () -> userService.getById(Long.MAX_VALUE));
    }

    @Test
    void getAll() {
        TypedQuery<Long> query = em.createQuery("Select count(*) from UserEntity u", Long.class);
        Long count = query.getSingleResult();
        assertEquals(count, userService.getAll().size());
    }

    @Test
    void create() {
        UserCreateRequestDto userCreateRequestDto =
                prepareUserCreateRequestDto("name5", "email5@gmail.com");
        userService.create(userCreateRequestDto);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.email = :email", UserEntity.class);
        UserEntity userEntity = query.setParameter("email", userCreateRequestDto.getEmail())
                .getSingleResult();

        assertThat(userEntity.getId(), notNullValue());
        assertThat(userEntity.getName(), equalTo(userCreateRequestDto.getName()));
        assertThat(userEntity.getEmail(), equalTo(userCreateRequestDto.getEmail()));

        assertThrows(ConflictException.class, () -> userService.create(userCreateRequestDto));
    }

    @Test
    void update() {
        Long id = userService.getAll().getFirst().getId();
        UserUpdateRequestDto userUpdateRequestDto =
                prepareUserUpdateRequestDto(id, "name11", "email11@gmail.com");
        userService.update(id, userUpdateRequestDto);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.id = :id", UserEntity.class);
        UserEntity userEntity = query.setParameter("id", userUpdateRequestDto.getId())
                .getSingleResult();

        assertThat(userEntity.getId(), equalTo(userUpdateRequestDto.getId()));
        assertThat(userEntity.getName(), equalTo(userUpdateRequestDto.getName()));
        assertThat(userEntity.getEmail(), equalTo(userUpdateRequestDto.getEmail()));

        assertThrows(NotFoundException.class, () -> userService.update(Long.MAX_VALUE, userUpdateRequestDto));
        assertThrows(ValidationException.class, () -> userService.update(null, userUpdateRequestDto));

        UserUpdateRequestDto userUpdateRequestDto1 =
                prepareUserUpdateRequestDto(null, "name11", "email2@gmail.com");
        assertThrows(ConflictException.class, () -> userService.update(id, userUpdateRequestDto1));

        UserUpdateRequestDto userUpdateRequestDto2 =
                prepareUserUpdateRequestDto(id, null, null);
        userService.update(id, userUpdateRequestDto2);

        userEntity = query.setParameter("id", userUpdateRequestDto.getId()).getSingleResult();

        assertThat(userEntity.getId(), equalTo(userUpdateRequestDto.getId()));
        assertThat(userEntity.getName(), equalTo(userUpdateRequestDto.getName()));
        assertThat(userEntity.getEmail(), equalTo(userUpdateRequestDto.getEmail()));
    }

    @Test
    void deleteById() {
        UserCreateRequestDto userCreateRequestDto =
                prepareUserCreateRequestDto("name6", "email6@gmail.com");
        UserDto userDto = userService.create(userCreateRequestDto);

        userService.deleteById(userDto.getId());
        assertThrows(NotFoundException.class, () -> userService.getById(userDto.getId()));
    }

    private UserCreateRequestDto prepareUserCreateRequestDto(String name, String email) {
        return new UserCreateRequestDto(name, email);
    }

    private UserUpdateRequestDto prepareUserUpdateRequestDto(Long id, String name, String email) {
        return new UserUpdateRequestDto(id, name, email);
    }
}