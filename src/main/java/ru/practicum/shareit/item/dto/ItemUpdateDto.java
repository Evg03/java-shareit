package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.validation.NullOrNotBlank;

@Data
@AllArgsConstructor
public class ItemUpdateDto {
    @NullOrNotBlank(message = "Имя должно быть null или не быть пустым")
    @EqualsAndHashCode.Exclude
    private String name;
    @NullOrNotBlank(message = "Описание должно быть null или не быть пустым")
    @EqualsAndHashCode.Exclude
    private String description;
    @EqualsAndHashCode.Exclude
    private Boolean available;
}
