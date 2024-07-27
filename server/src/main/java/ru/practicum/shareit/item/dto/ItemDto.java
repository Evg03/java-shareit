package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.GetItemBookingDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private GetItemBookingDto lastBooking;
    private GetItemBookingDto nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;
}

