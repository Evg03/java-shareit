package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userRepository.save(new User(null, userDto.getName(), userDto.getEmail()));
        log.info("Пользователь с id = {} добавлен", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public UserDto updateUser(int userId, UserUpdateDto userUpdateDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        User user = userOptional.get();
        String newEmail = userUpdateDto.getEmail();
        if (newEmail != null) user.setEmail(newEmail);
        String newName = userUpdateDto.getName();
        if (newName != null) {
            user.setName(newName);
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удалён", userId);
    }

    @Override
    public List<UserDto> getUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Количество пользователей = {}", users.size());
        return users;
    }
}
