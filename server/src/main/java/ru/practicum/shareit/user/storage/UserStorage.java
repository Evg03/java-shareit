package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User getUser(int userId);

    User updateUser(User user);

    void deleteUser(int userId);

    List<User> getUsers();

    boolean containsEmail(String email);
}
