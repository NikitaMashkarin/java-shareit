package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {


    private final UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "desc1",
            userDto,
            LocalDateTime.now(),
            List.of());

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createRequest() throws Exception {
        when(itemRequestService.create(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", Matchers.is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", Matchers.notNullValue()))
                .andExpect(jsonPath("$.created", Matchers.notNullValue()))
                .andExpect(jsonPath("$.items", Matchers.notNullValue()));
    }

    @Test
    void getUserItemRequests() throws Exception {
        when(itemRequestService.getAllByRequestorId(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", Matchers.is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].created", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].items", Matchers.notNullValue()));
    }

    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.getAll())
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", Matchers.is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].created", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].items", Matchers.notNullValue()));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getById(anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", Matchers.is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", Matchers.notNullValue()))
                .andExpect(jsonPath("$.created", Matchers.notNullValue()))
                .andExpect(jsonPath("$.items", Matchers.notNullValue()));
    }
}