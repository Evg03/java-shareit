package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentCreateDto commentCreateDto, @PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemClient.addComment(itemId, userId, commentCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemUpdateDto itemUpdateDto, @RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemClient.updateItem(itemId, userId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemClient.searchByText(text, userId);
    }
}
