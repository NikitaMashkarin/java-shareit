package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {

    private final JacksonTester<UserDto> userDtoJacksonTester;
    private final JacksonTester<UserCreateRequestDto> userCreateRequestDtoJacksonTester;
    private final JacksonTester<UserUpdateRequestDto> userUpdateRequestDtoJacksonTester;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com");

        JsonContent<UserDto> result = userDtoJacksonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

    @Test
    void testUserCreateRequestDto() throws Exception {
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto(
                "John",
                "john.doe@mail.com");

        JsonContent<UserCreateRequestDto> result = userCreateRequestDtoJacksonTester.write(userCreateRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

    @Test
    void testUserUpdateRequestDto() throws Exception {
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(
                1L,
                "John",
                "john.doe@mail.com");

        JsonContent<UserUpdateRequestDto> result = userUpdateRequestDtoJacksonTester.write(userUpdateRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }
}