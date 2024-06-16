package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectOwnerIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {

    @Autowired
    @Qualifier("InMemoryItemStorage")
    private final ItemStorage itemStorage;
    @Autowired
    @Qualifier("InMemoryUserStorage")
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        if (userStorage.getUser(userId) == null) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        Item item = itemStorage.addItem(new Item(0,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                null));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto, int userId, int itemId) {
        Item item = itemStorage.getItem(itemId);
        if (item == null) {
            log.warn("Item'a с id = {} не существует", itemId);
            throw new ItemNotFoundException(String.format("Item'a с id = %s не существует", itemId));
        }
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        if (item.getOwner() != userId) {
            log.warn("Пользователь с id = {} не является владельцем item'а c id = {}", userId, itemId);
            throw new IncorrectOwnerIdException(String.format("Пользователь с id = %s не является " +
                    "владельцем item'а c id = %s. Только владелец может редактировать item.", userId, itemId));
        }
        String name = itemUpdateDto.getName();
        String description = itemUpdateDto.getDescription();
        Boolean available = itemUpdateDto.getAvailable();
        if (name != null) item.setName(name);
        if (description != null) item.setDescription(description);
        if (available != null) item.setAvailable(available);
        itemStorage.updateItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(int itemId) {
        Item item = itemStorage.getItem(itemId);
        if (item == null) {
            log.warn("Item'a с id = {} не существует", itemId);
            throw new ItemNotFoundException(String.format("Item'a с id = %s не существует", itemId));
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(int userId) {
        return itemStorage.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) return new ArrayList<>();
        List<ItemDto> items = itemStorage.searchByText(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        log.info("По запросу: \"{}\" найдено item'ов: {}", text, items.size());
        return items;
    }
}
