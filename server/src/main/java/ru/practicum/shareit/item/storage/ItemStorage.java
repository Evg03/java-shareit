package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item getItem(int itemId);

    Item updateItem(Item item);

    List<Item> getItems(int userId);

    List<Item> searchByText(String text);
}
