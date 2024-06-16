package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.validation.NullOrNotBlank;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class UserUpdateDto {
    @NullOrNotBlank(message = "Имя должно быть null или не быть пустым")
    @EqualsAndHashCode.Exclude
    private String name;
    @Email(message = "Некорректный email")
    private String email;
}
