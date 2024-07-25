package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addRequest(ItemRequestCreateDto itemRequestCreateDto, int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        ItemRequest itemRequest = new ItemRequest(null,
                itemRequestCreateDto.getDescription(),
                userId,
                LocalDateTime.now(),
                new ArrayList<>());
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequests(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        return requestRepository.findAllByRequestorOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int from, int size, int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        List<ItemRequest> requests = requestRepository.findAllByRequestorNotOrderByCreatedDesc(userId,
                PageRequest.of(from, size));
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(int requestId, int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        Optional<ItemRequest> request = requestRepository.findById(requestId);
        if (request.isEmpty()) {
            log.warn("Request'a с id = {} не существует", requestId);
            throw new RequestNotFoundException(String.format("Request'a с id = %s не существует", requestId));
        }
        return ItemRequestMapper.toItemRequestDto(request.get());
    }
}
