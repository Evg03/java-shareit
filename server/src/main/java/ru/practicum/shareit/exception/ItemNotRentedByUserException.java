package ru.practicum.shareit.exception;

public class ItemNotRentedByUserException extends RuntimeException {
    public ItemNotRentedByUserException(String message) {
        super(message);
    }
}
