package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto addRequest(ItemRequestCreateDto itemRequestCreateDto, int userId);

    List<ItemRequestDto> getRequests(int userId);

    List<ItemRequestDto> getAllRequests(int from, int size, int userId);

    ItemRequestDto getRequestById(int requestId, int userId);
}
