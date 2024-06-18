package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto userDto) {
        String email = userDto.getEmail();
        if (userStorage.containsEmail(email)) {
            throw new EmailAlreadyExistsException(String.format("Пользователь с email = %s уже существует", email));
        }
        User user = userStorage.addUser(new User(0, userDto.getName(), email));
        log.info("Пользователь с id = {} добавлен", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(int userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int userId, UserUpdateDto userUpdateDto) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        String newEmail = userUpdateDto.getEmail();
        if (newEmail != null && !user.getEmail().equals(newEmail)) {
            if (userStorage.containsEmail(newEmail)) {
                throw new EmailAlreadyExistsException(String.format("Пользователь с email = %s уже существует", newEmail));
            }
            user.setEmail(newEmail);
        }
        String newName = userUpdateDto.getName();
        if (newName != null) {
            user.setName(newName);
        }
        userStorage.updateUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(int userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        userStorage.deleteUser(userId);
        log.info("Пользователь с id = {} удалён", userId);
    }

    @Override
    public List<UserDto> getUsers() {
        List<UserDto> users = userStorage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Количество пользователей = {}", users.size());
        return users;
    }
}
