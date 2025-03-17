package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


public interface UserService {

    UserDto getById(Long id);

    List<UserDto> getAll();

    UserDto create(UserDto userCreateRequestDto);

    UserDto update(Long id, UserDto userUpdateRequestDto);

    void deleteById(Long id);
}
