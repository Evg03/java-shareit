package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody @Valid BookingCreateDto bookingCreateDto,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.addBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable int bookingId, @RequestParam boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getAllUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserItemsBookings(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getAllUserItemsBookings(state, userId);
    }

}
