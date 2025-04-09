package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private final UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com");

    private final UserDto userDto2 = new UserDto(
            2L,
            "John2",
            "john2.doe@mail.com");

    private final ItemDto itemDto = new ItemDto(
            1L,
            "name1",
            "desc1",
            true,
            userDto,
            null,
            null,
            null,
            null);

    private final ItemDto itemDto2 = new ItemDto(
            2L,
            "name2",
            "desc2",
            false,
            userDto2,
            null,
            null,
            null,
            null);

    private final CommentDto commentDto = new CommentDto(
            1L,
            "123",
            new ItemEntity(),
            "name1",
            LocalDateTime.now()
    );

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void getItemById() throws Exception {
        when(itemService.getById(1L))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", Matchers.is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.lastBooking", Matchers.is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", Matchers.is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", Matchers.is(itemDto.getComments())))
                .andExpect(jsonPath("$.request", Matchers.is(itemDto.getRequest())));
    }

    @Test
    void addItem() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(jsonPath("$.owner.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.lastBooking", Matchers.is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", Matchers.is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", Matchers.is(itemDto.getComments())))
                .andExpect(jsonPath("$.request", Matchers.is(itemDto.getRequest())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(jsonPath("$.owner.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.lastBooking", Matchers.is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", Matchers.is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", Matchers.is(itemDto.getComments())))
                .andExpect(jsonPath("$.request", Matchers.is(itemDto.getRequest())));

        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(new ForbiddenException("item update forbidden"));
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    void getAllUserItems() throws Exception {
        when(itemService.getAllByUser(anyLong()))
                .thenReturn(List.of(itemDto, itemDto2));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[*].name",
                        Matchers.containsInAnyOrder(itemDto.getName(), itemDto2.getName())))
                .andExpect(jsonPath("$[*].description",
                        Matchers.containsInAnyOrder(itemDto.getDescription(), itemDto2.getDescription())));
    }

    @Test
    void getItemsByText() throws Exception {
        String textFilter = "123";
        when(itemService.getAllAvailableByText(textFilter))
                .thenReturn(List.of(itemDto, itemDto2));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("text", textFilter)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[*].name",
                        Matchers.containsInAnyOrder(itemDto.getName(), itemDto2.getName())))
                .andExpect(jsonPath("$[*].description",
                        Matchers.containsInAnyOrder(itemDto.getDescription(), itemDto2.getDescription())));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(commentDto.getId()), Long.class));
    }
}