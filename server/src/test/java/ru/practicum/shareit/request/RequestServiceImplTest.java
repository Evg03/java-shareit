package ru.practicum.shareit.request;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;

    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "test@mail.ru");
        request = new ItemRequest(
                1,
                "description",
                1,
                LocalDateTime.now(),
                new ArrayList<>()
        );
    }

    @Test
    public void addRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ItemRequestDto requestDto = requestService.addRequest(new ItemRequestCreateDto("description"), 1);
        Assertions.assertNotNull(requestDto);
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    public void addRequestUserNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> requestService.addRequest(new ItemRequestCreateDto("description"), 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getRequests() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorOrderByCreatedDesc(anyInt())).thenReturn(new ArrayList<>());
        List<ItemRequestDto> requests = requestService.getRequests(1);
        Assertions.assertNotNull(requests);
        Assertions.assertTrue(requests.isEmpty());
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).findAllByRequestorOrderByCreatedDesc(anyInt());
    }

    @Test
    public void getRequestsUserNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> requestService.getRequests(1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllRequests() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorNotOrderByCreatedDesc(anyInt(),
                any())).thenReturn(new ArrayList<>());
        List<ItemRequestDto> requests = requestService.getAllRequests(1, 1, 1);
        Assertions.assertNotNull(requests);
        Assertions.assertTrue(requests.isEmpty());
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).findAllByRequestorNotOrderByCreatedDesc(anyInt(),
                any());
    }

    @Test
    public void getAllRequestsUserNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> requestService.getAllRequests(1, 1, 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getRequestById() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        ItemRequestDto requestDto = requestService.getRequestById(1, 1);
        Assertions.assertNotNull(requestDto);
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).findById(anyInt());
    }

    @Test
    public void getRequestByIdNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(RequestNotFoundException.class, () -> requestService.getRequestById(1, 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getRequestByIdUserNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> requestService.getRequestById(1, 1));
        verify(userRepository, times(1)).findById(any());
    }
}
