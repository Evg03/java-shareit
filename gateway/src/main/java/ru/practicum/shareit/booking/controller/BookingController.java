package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.client.BookingClient;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingCreateDto bookingCreateDto,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingClient.addBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable int bookingId, @RequestParam boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingClient.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserItemsBookings(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingClient.getAllUserItemsBookings(userId, state);
    }

}
