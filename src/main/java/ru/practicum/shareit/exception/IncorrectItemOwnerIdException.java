package ru.practicum.shareit.exception;

public class IncorrectItemOwnerIdException extends RuntimeException {
    public IncorrectItemOwnerIdException(String message) {
        super(message);
    }
}
