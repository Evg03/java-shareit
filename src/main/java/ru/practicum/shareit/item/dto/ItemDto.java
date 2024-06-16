package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Имя не должно быть пустым или null")
    @EqualsAndHashCode.Exclude
    private String name;
    @NotBlank(message = "Описание не должно быть пустым или null")
    @EqualsAndHashCode.Exclude
    private String description;
    @NotNull(message = "Статус вещи не может быть null")
    @EqualsAndHashCode.Exclude
    private Boolean available;
}

