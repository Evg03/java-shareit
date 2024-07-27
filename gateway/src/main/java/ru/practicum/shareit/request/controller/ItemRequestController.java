package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody @Valid ItemRequestCreateDto itemRequestCreateDto,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        return requestClient.addRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") int userId) {

        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam int from,
                                                 @RequestParam int size,
                                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        return requestClient.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable int requestId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return requestClient.getRequestById(userId, requestId);
    }
}
