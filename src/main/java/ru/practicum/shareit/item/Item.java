package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {
    @NotNull
    private Integer id;
    @NotBlank(message = "Имя не должно быть пустым или null")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым или null")
    private String description;
    @NotNull(message = "Статус вещи не может быть null")
    private boolean available;
    @NotNull
    private Integer owner;
    private ItemRequest request;
}
