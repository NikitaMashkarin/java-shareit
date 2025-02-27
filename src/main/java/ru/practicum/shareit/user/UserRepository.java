package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> findAll();

    Optional<User> getUser(long id);

    User create(User user);

    User update(UserDto user, Long id);

    void delete(long id);
}
