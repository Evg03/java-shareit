package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestBody @Valid ItemRequestCreateDto itemRequestCreateDto,
                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        return requestService.addRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") int userId) {

        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam int from,
                                               @RequestParam int size,
                                               @RequestHeader("X-Sharer-User-Id") int userId) {
        return requestService.getAllRequests(from,size,userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable int requestId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return requestService.getRequestById(requestId, userId);
    }



}
