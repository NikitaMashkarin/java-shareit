package ru.practicum.shareit.user;

import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));
        log.debug("Get user with id = {}", id);
        return userMapper.toUserDto(userEntity);
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("Get all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto create(UserCreateRequestDto userCreateRequestDto) {
        if (userRepository.getUserCountByEmail(userCreateRequestDto.getEmail()) > 0) {
            throw new ConflictException("Email already exists");
        }

        UserEntity userEntity = userMapper.toUserEntity(userCreateRequestDto);
        UserEntity createdUserEntity = userRepository.save(userEntity);
        log.debug("User was created with id = {}", createdUserEntity.getId());
        return userMapper.toUserDto(createdUserEntity);
    }

    @Transactional(rollbackFor = {IOException.class, SQLException.class})
    @Override
    public UserDto update(Long id, UserUpdateRequestDto userUpdateRequestDto) {
        if (id == null) {
            throw new ValidationException("Id must not be empty");
        }
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id = " + id);
        }
        if (userRepository.getUserCountByEmail(userUpdateRequestDto.getEmail()) > 0) {
            throw new ConflictException("Email already exists");
        }

        UserEntity userEntity = userMapper.toUserEntity(userUpdateRequestDto);
        UserEntity userEntityForUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));

        userEntityForUpdate.setName(!StringUtil.isNullOrEmpty(userEntity.getName()) ?
                userEntity.getName() : userEntityForUpdate.getName());
        userEntityForUpdate.setEmail(!StringUtil.isNullOrEmpty(userEntity.getEmail()) ?
                userEntity.getEmail() : userEntityForUpdate.getEmail());

        UserEntity updatedUserEntity = userRepository.save(userEntityForUpdate);
        log.debug("User with id = {} was updated", id);
        return userMapper.toUserDto(updatedUserEntity);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        log.debug("User with id = {} was deleted", id);
    }
}