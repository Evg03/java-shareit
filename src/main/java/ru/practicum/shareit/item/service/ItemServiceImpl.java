package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.service.BookingStatus;
import ru.practicum.shareit.booking.dto.GetItemBookingDto;
import ru.practicum.shareit.exception.IncorrectItemOwnerIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemNotRentedByUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        Item item = itemRepository.save(new Item(null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                null));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto, int userId, int itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.warn("Item'a с id = {} не существует", itemId);
            throw new ItemNotFoundException(String.format("Item'a с id = %s не существует", itemId));
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        Item item = itemOptional.get();
        if (item.getOwner() != userId) {
            log.warn("Пользователь с id = {} не является владельцем item'а c id = {}", userId, itemId);
            throw new IncorrectItemOwnerIdException(String.format("Пользователь с id = %s не является " +
                    "владельцем item'а c id = %s. Только владелец может редактировать item.", userId, itemId));
        }
        String name = itemUpdateDto.getName();
        String description = itemUpdateDto.getDescription();
        Boolean available = itemUpdateDto.getAvailable();
        if (name != null) item.setName(name);
        if (description != null) item.setDescription(description);
        if (available != null) item.setAvailable(available);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(int itemId, int userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.warn("Item'a с id = {} не существует", itemId);
            throw new ItemNotFoundException(String.format("Item'a с id = %s не существует", itemId));
        }
        Item item = itemOptional.get();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().equals(userId)) {
            LocalDateTime currentDate = LocalDateTime.now();
            GetItemBookingDto lastBooking = getLastBooking(itemId, currentDate);
            GetItemBookingDto nextBooking = getNextBooking(itemId, currentDate);
            itemDto.setNextBooking(nextBooking);
            itemDto.setLastBooking(lastBooking);
        }
        itemDto.setComments(commentRepository.findByItem_idOrderByCreatedDesc(itemDto.getId()));
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(int userId) {
        List<ItemDto> items = itemRepository.findAllByOwnerOrderByIdAsc(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        LocalDateTime currentDate = LocalDateTime.now();
        for (ItemDto itemDto : items) {
            GetItemBookingDto lastBooking = getLastBooking(itemDto.getId(), currentDate);
            GetItemBookingDto nextBooking = getNextBooking(itemDto.getId(), currentDate);
            itemDto.setNextBooking(nextBooking);
            itemDto.setLastBooking(lastBooking);
            itemDto.setComments(commentRepository.findByItem_idOrderByCreatedDesc(itemDto.getId()));
        }
        return items;
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) return new ArrayList<>();
        List<ItemDto> items = itemRepository.findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue(text,
                text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        log.info("По запросу: \"{}\" найдено item'ов: {}", text, items.size());
        return items;
    }

    @Override
    public CommentDto addComment(int itemId, int userId, CommentCreateDto commentCreateDto) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.warn("Item'a с id = {} не существует", itemId);
            throw new ItemNotFoundException(String.format("Item'a с id = %s не существует", itemId));
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        Item item = itemOptional.get();
        User user = userOptional.get();
        String text = commentCreateDto.getText();
        if (!isRentedByUser(itemId, userId)) {
            throw new ItemNotRentedByUserException(String.format("Пользователь с id = %s не брал в аренду Item c id = %s. " +
                    "Отзыв может оставить только тот пользователь, который брал вещь в аренду.", userId, itemId));
        }
        Comment comment = new Comment(null, text, item, user, LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private GetItemBookingDto getLastBooking(int itemId, LocalDateTime currentDate) {
        List<Booking> endedBookings = bookingRepository.findByItem_idAndStartBeforeOrderByStartDesc(itemId, currentDate);
        if (endedBookings.isEmpty()) return null;
        return BookingMapper.toGetItemBookingDto(endedBookings.get(0));
    }

    private GetItemBookingDto getNextBooking(int itemId, LocalDateTime currentDate) {
        List<Booking> futureBookings = bookingRepository.findByItem_idAndStartAfterAndStatusNotOrderByStartAsc(itemId, currentDate, BookingStatus.REJECTED.name());
        if (futureBookings.isEmpty()) return null;
        return BookingMapper.toGetItemBookingDto(futureBookings.get(0));
    }

    private boolean isRentedByUser(int itemId, int userId) {
        List<Booking> bookings = bookingRepository.findByItem_idAndBooker_idAndEndBeforeAndStatusNot(itemId,
                userId,
                LocalDateTime.now(),
                BookingStatus.REJECTED.name());
        return !bookings.isEmpty();
    }
}
