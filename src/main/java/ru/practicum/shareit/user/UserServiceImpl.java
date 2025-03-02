package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return repository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public UserDto getUser(long id) {
        return userMapper.toUserDto(repository.getUser(id));
    }

    public UserDto create(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new ValidationException("Email должен быть указан");
        repository.create(user);
        return userMapper.toUserDto(user);
    }

    public UserDto update(UserDto user, Long id) {
        if (id == 0) throw new ValidationException("Id должен быть указан");
        return userMapper.toUserDto(repository.update(user, id));
    }

    public void delete(long id) {
        repository.delete(id);
    }
}
