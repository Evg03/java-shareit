package ru.practicum.shareit.user;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "test@mail.ru");
    }

    @Test
    public void addUser() {
        UserDto userDto = new UserDto(null, "name", "test@mail.ru");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UserDto addedUser = userService.addUser(userDto);
        Assertions.assertNotNull(addedUser);
        Assertions.assertEquals(userDto, addedUser);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void getUser() {
        UserDto expectedUserDto = new UserDto(1, "name", "test@mail.ru");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUser(1);
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(userDto, expectedUserDto);
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getUserNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> userService.getUser(1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void updateUser() {
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UserUpdateDto userUpdateDto = new UserUpdateDto("name", "test@mail.ru");
        UserDto updatedUser = userService.updateUser(1, userUpdateDto);
        Assertions.assertNotNull(updatedUser);
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void updateUserNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> userService.updateUser(1,
                new UserUpdateDto("name", "test@mail.ru")));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void deleteUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        userService.deleteUser(1);
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    public void deleteUserNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getUsers() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<UserDto> users = userService.getUsers();
        Assertions.assertNotNull(users);
        Assertions.assertTrue(users.isEmpty());
    }

}
