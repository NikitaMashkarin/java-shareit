package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;


public interface UserService {

    User findById(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
