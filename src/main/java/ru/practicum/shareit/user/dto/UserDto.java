package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    Integer id;
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Email(message = "Некорректный email")
    @NotNull(message = "Email не должен быть null")
    private String email;
}
