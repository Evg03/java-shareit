package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingCreateDto bookingCreateDto, int userId);

    BookingDto approveBooking(int bookingId, int userId, boolean approved);

    BookingDto getBookingById(int bookingId, int userId);

    List<BookingDto> getAllUserBookings(String state, int userId);

    List<BookingDto> getAllUserItemsBookings(String state, int userId);
}
