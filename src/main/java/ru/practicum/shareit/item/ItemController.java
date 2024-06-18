package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody @Valid ItemUpdateDto itemUpdateDto, @RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.updateItem(itemUpdateDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
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
