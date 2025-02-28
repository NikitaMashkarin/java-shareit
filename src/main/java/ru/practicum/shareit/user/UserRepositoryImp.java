package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
public class UserRepositoryImp implements UserRepository {
    Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> getUser(long id) {
        return Optional.of(users.get(id));
    }

    public User create(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) throw new DuplicatedDataException("Email уже используется");
        }
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(UserDto user, Long id) {
        //users.remove(user.getId());
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }

        if (user.getEmail() != null) {
            for (User u : users.values()) {
                if (u.getEmail() != null && u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), id)) {
                    throw new DuplicatedDataException("Email уже используется");
                }
            }
        }

        User user1 = new User(id, user.getName(), user.getEmail());
        users.put(id, user1);

        return user1;
    }

    public void delete(long id) {
        if (!users.containsKey(id)) throw new ValidationException("Пользователь  с id: " + id + " не найден");
        users.remove(id);
    }

    private long getId() {
        long lastId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
