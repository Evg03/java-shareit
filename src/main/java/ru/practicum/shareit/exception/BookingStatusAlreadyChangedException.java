package ru.practicum.shareit.exception;

public class BookingStatusAlreadyChangedException extends RuntimeException {
    public BookingStatusAlreadyChangedException(String message) {
        super(message);
    }
}
