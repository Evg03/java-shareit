package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    Integer id;
    @NotBlank(message = "Имя не должно быть пустым")
    @EqualsAndHashCode.Exclude
    private String name;
    @Email(message = "Некорректный email")
    @NotNull(message = "Email не должен быть null")
    private String email;
}
