package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetItemBookingDto;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static GetItemBookingDto toGetItemBookingDto(Booking booking) {
        return new GetItemBookingDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
