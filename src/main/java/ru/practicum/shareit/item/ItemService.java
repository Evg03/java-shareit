package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto, int userId, int itemId);

    ItemDto getItem(int itemId);

    List<ItemDto> getItems(int userId);

    List<ItemDto> searchByText(String text);
}
