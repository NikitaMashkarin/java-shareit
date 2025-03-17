package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.UserEntity;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    private UserEntity requestor;
    private LocalDateTime created;
}
