package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    public void getUsers() throws Exception {
        when(userService.getUsers()).thenReturn(new ArrayList<>());
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]")
                );
        verify(userService, times(1)).getUsers();
    }

    @Test
    public void getUser() throws Exception {
        UserDto userDto = new UserDto(1, "name", "test@mail.ru");
        when(userService.getUser(1)).thenReturn(userDto);
        mvc.perform(get("/users/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail()))
                );
        verify(userService, times(1)).getUser(anyInt());
    }

    @Test
    public void createUser() throws Exception {
        UserDto userDto = new UserDto(1, "name", "test@mail.ru");
        when(userService.addUser(any(UserDto.class))).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail()))
                );
        verify(userService, times(1)).addUser(any());
    }

    @Test
    public void updateUser() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto("newName", "new@mail.ru");
        UserDto updatedUserDto = new UserDto(1, "newName", "new@mail.ru");
        when(userService.updateUser(anyInt(), any(UserUpdateDto.class))).thenReturn(updatedUserDto);
        mvc.perform(patch("/users/" + 1)
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(updatedUserDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(updatedUserDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(updatedUserDto.getEmail()))
                );
        verify(userService, times(1)).updateUser(anyInt(), any());
    }

    @Test
    public void deleteUser() throws Exception {
        mvc.perform(delete("/users/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()
                );
        verify(userService, times(1)).deleteUser(anyInt());
    }
}
