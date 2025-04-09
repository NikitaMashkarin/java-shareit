package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    private final UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com");
    private final UserDto userDto2 = new UserDto(
            2L,
            "John2",
            "john2.doe@mail.com");
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveNewUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail())));

        when(userService.create(any()))
                .thenThrow(new ValidationException("user validation exception"));
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getById(1L))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail())));

        when(userService.getById(2L))
                .thenThrow(new NotFoundException("user not found"));
        mvc.perform(get("/users/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDto, userDto2));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder("John", "John2")))
                .andExpect(jsonPath("$[*].email",
                        Matchers.containsInAnyOrder("john.doe@mail.com", "john2.doe@mail.com")));

        mvc.perform(get("/users/error")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(anyLong(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail())));

        when(userService.update(anyLong(), any()))
                .thenThrow(new ConflictException("user validation exception"));
        mvc.perform(patch("/users/2")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteById(anyLong());

        mvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}