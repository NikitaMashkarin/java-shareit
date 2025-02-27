package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
