package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository repository;
    private final UserMapper userMapper;

    public List<UserDto> findAll(){
        return repository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public UserDto getUser(long id) {
        Optional<User> user = repository.getUser(id);
        if(user.isEmpty()) throw new ValidationException("Пользователь  с id: " + id + " не найден");
        return userMapper.toUserDto(user.get());
    }

    public UserDto create(User user) {
        if(user.getEmail() == null || user.getEmail().isEmpty()) throw new ValidationException("Email должен быть указан");
        repository.create(user);
        return userMapper.toUserDto(user);
    }

    public UserDto update(UserDto user, Long id) {
        if(id == 0)  throw new ValidationException("Id должен быть указан");
        return userMapper.toUserDto(repository.update(user, id));
    }

    public void delete(long id) {
        repository.delete(id);
    }
}
