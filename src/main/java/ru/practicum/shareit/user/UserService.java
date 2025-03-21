package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import java.util.List;

public interface UserService {

    UserDto getById(Long id);

    List<UserDto> getAll();

    UserDto create(UserCreateRequestDto userCreateRequestDto);

    UserDto update(Long id, UserUpdateRequestDto userUpdateRequestDto);

    void deleteById(Long id);
}