package ru.practicum.shareit.user;

import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(Long id) {
        User userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));
        return userMapper.toUserDto(userEntity);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto create(UserDto userCreateRequestDto) {
        if(userCreateRequestDto.getEmail().isEmpty()) throw new ValidationException("Email ");

        if (userRepository.getUserCountByEmail(userCreateRequestDto.getEmail()) > 0)
            throw new DuplicatedDataException("Email already exists");


        User userEntity = userMapper.toUserEntity(userCreateRequestDto);
        User createdUserEntity = userRepository.save(userEntity);
        return userMapper.toUserDto(createdUserEntity);
    }

    @Override
    public UserDto update(Long id, UserDto userUpdateRequestDto) {
        if (id == null) {
            throw new ValidationException("Id must not be empty");
        }
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id = " + id);
        }
        if (userRepository.getUserCountByEmail(userUpdateRequestDto.getEmail()) > 0) {
            throw new DuplicatedDataException("Email already exists");
        }

        User userEntity = userMapper.toUserEntity(userUpdateRequestDto);
        User userEntityForUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));

        userEntityForUpdate.setName(!StringUtil.isNullOrEmpty(userEntity.getName()) ?
                userEntity.getName() : userEntityForUpdate.getName());
        userEntityForUpdate.setEmail(!StringUtil.isNullOrEmpty(userEntity.getEmail()) ?
                userEntity.getEmail() : userEntityForUpdate.getEmail());

        User updatedUserEntity = userRepository.save(userEntityForUpdate);
        return userMapper.toUserDto(updatedUserEntity);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
