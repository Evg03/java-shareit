package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetItemBookingDto {
    private Integer id;
    private Integer bookerId;
}
