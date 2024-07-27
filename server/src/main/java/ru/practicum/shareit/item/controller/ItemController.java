package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentCreateDto commentCreateDto, @PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.addComment(itemId, userId, commentCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemUpdateDto itemUpdateDto, @RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.updateItem(itemUpdateDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        return itemService.searchByText(text);
    }
}
