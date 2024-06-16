package ru.practicum.shareit.exception;

public class IncorrectOwnerIdException extends RuntimeException {
    public IncorrectOwnerIdException(String message) {
        super(message);
    }
}
