package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer item;
    private Integer booker;
    private String status;
}
