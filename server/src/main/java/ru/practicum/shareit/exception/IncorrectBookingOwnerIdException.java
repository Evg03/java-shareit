package ru.practicum.shareit.exception;

public class IncorrectBookingOwnerIdException extends RuntimeException {
    public IncorrectBookingOwnerIdException(String message) {
        super(message);
    }
}
