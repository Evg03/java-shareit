package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto user);

    UserDto getUser(int userId);

    UserDto updateUser(int userId, UserUpdateDto userUpdateDto);

    void deleteUser(int userId);

    List<UserDto> getUsers();
}
