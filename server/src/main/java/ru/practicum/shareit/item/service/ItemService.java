package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto, int userId, int itemId);

    ItemDto getItem(int itemId, int userId);

    List<ItemDto> getItems(int userId);

    List<ItemDto> searchByText(String text);

    CommentDto addComment(int itemId, int userId, CommentCreateDto commentCreateDto);
}
