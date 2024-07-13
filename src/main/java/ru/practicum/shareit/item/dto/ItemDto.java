package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.GetItemBookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Имя не должно быть пустым или null")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым или null")
    private String description;
    @NotNull(message = "Статус вещи не может быть null")
    private Boolean available;
    private GetItemBookingDto lastBooking;
    private GetItemBookingDto nextBooking;
    private List<CommentDto> comments;
}

