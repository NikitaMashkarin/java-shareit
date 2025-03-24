package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com");

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

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2024, 3, 12, 10, 15),
            LocalDateTime.of(2024, 3, 15, 15, 0),
            itemDto,
            userDto,
            BookingStatus.WAITING
    );

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", Matchers.is(bookingDto.getStart().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$.end", Matchers.is(bookingDto.getEnd().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$.status", Matchers.is(bookingDto.getStatus().name()),
                        BookingStatus.class));
    }

    @Test
    void getAllUserBookings() throws Exception {
        when(bookingService.getAllByState(anyLong(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", Matchers.is(bookingDto.getStart().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$[0].end", Matchers.is(bookingDto.getEnd().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$[0].item", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].status", Matchers.is(bookingDto.getStatus().name()),
                        BookingStatus.class));
    }

    @Test
    void getAllBookingsByOwner() throws Exception {
        when(bookingService.getAllByOwnerAndState(anyLong(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", Matchers.is(bookingDto.getStart().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$[0].end", Matchers.is(bookingDto.getEnd().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$[0].item", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].status", Matchers.is(bookingDto.getStatus().name()),
                        BookingStatus.class));
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", Matchers.is(bookingDto.getStart().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$.end", Matchers.is(bookingDto.getEnd().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$.status", Matchers.is(bookingDto.getStatus().name()),
                        BookingStatus.class));
    }

    @Test
    void setBookingApproval() throws Exception {
        when(bookingService.setApproval(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", Matchers.is(bookingDto.getStart().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$.end", Matchers.is(bookingDto.getEnd().format(pattern)),
                        LocalDateTime.class))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$.status", Matchers.is(bookingDto.getStatus().name()),
                        BookingStatus.class));
    }
}