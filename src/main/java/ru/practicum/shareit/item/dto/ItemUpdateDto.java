package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.NullOrNotBlank;

@Data
@AllArgsConstructor
public class ItemUpdateDto {
    @NullOrNotBlank(message = "Имя должно быть null или не быть пустым")
    private String name;
    @NullOrNotBlank(message = "Описание должно быть null или не быть пустым")
    private String description;
    private Boolean available;
}
