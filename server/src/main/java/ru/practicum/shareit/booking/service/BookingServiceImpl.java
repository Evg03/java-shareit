package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(BookingCreateDto bookingCreateDto, int userId) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        Integer itemId = bookingCreateDto.getItemId();
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("Item'a с id = %s не существует", itemId));
        }
        Item item = itemOptional.get();
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException(String.format("Item с id = %s не доступен для бронирования", itemId));
        }

        if (item.getOwner().equals(userId)) {
            throw new BookingOwnItemException(String.format("Пользователь с id = %s не может забронировать собственный " +
                    "Item с id = %s.", userId, itemId));
        }

        LocalDateTime start = bookingCreateDto.getStart();
        LocalDateTime end = bookingCreateDto.getEnd();
        if (start == null) {
            throw new IllegalArgumentException("Время начала бронирования не может быть null");
        }
        if (end == null) {
            throw new IllegalArgumentException("Время окончания бронирования не может быть null");
        }
        if (start.isEqual(end)) {
            throw new IllegalArgumentException("Время начала и окончания бронирования не должны совпадать");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Время начала бронирования не может быть позже времени окончания бонирования");
        }
        Booking booking = new Booking(null,
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                itemOptional.get(),
                userOptional.get(),
                BookingStatus.WAITING.name());
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(int bookingId, int userId, boolean approved) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new BookingNotFoundException(String.format("Бронирования с id = %s не существует", bookingId));
        }
        Booking booking = bookingOptional.get();
        if (!Objects.equals(booking.getStatus(), BookingStatus.WAITING.name())) {
            throw new BookingStatusAlreadyChangedException(String.format("Нельзя повторно изменить статус бронирования."));
        }
        Item item = booking.getItem();
        if (!item.getOwner().equals(userId)) {
            throw new IncorrectBookingOwnerIdException(String.format("Пользователь с id = %s не является " +
                    "владельцем item'а c id = %s. Только владелец item'a может подтвердить или отклонить запрос на " +
                    "бронирование.", userId, item.getId()));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED.name());
        } else {
            booking.setStatus(BookingStatus.REJECTED.name());
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(int bookingId, int userId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new BookingNotFoundException(String.format("Бронирования с id = %s не существует", bookingId));
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IncorrectItemOwnerIdException(String.format("Пользователя с id = %s не существует", userId));
        }
        Booking booking = bookingOptional.get();
        Integer bookerId = booking.getBooker().getId();
        Integer ownerId = booking.getItem().getOwner();
        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new IncorrectBookingOwnerIdException(String.format("Пользователь с id = %s не является " +
                            "владельцем item'а c id = %s или владельцем бронирования с id =%s. Только владелец " +
                            "item'a или бронирования имеет доступ.",
                    userId,
                    booking.getItem().getId(),
                    booking.getId()));
        }
        return BookingMapper.toBookingDto(bookingOptional.get());
    }

    @Override
    public List<BookingDto> getAllUserBookings(String state, int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        State filterState;
        try {
            filterState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(String.format("Фильтр '" + state + "' не поддерживается.", state));
        }
        switch (filterState) {
            case CURRENT:
                return bookingRepository.findByBooker_idAndStartBeforeAndEndAfterOrderByBooker_idDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBooker_idAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId,
                                BookingStatus.WAITING.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId,
                                BookingStatus.REJECTED.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case ALL:
            default:
                return bookingRepository.findByBooker_idOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getAllUserItemsBookings(String state, int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        State filterState;
        try {
            filterState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(String.format("Фильтр '" + state + "' не поддерживается.", state));
        }

        List<Integer> itemsIds = itemRepository.findAllByOwnerOrderByIdAsc(userId).stream().map(Item::getId).collect(Collectors.toList());
        switch (filterState) {
            case CURRENT:
                return bookingRepository.findByItem_idInAndStartBeforeAndEndAfterOrderByStartDesc(itemsIds,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByItem_idInAndEndBeforeOrderByStartDesc(itemsIds, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItem_idInAndStartAfterOrderByStartDesc(itemsIds, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByItem_idInAndStatusOrderByStartDesc(itemsIds,
                                BookingStatus.WAITING.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByItem_idInAndStatusOrderByStartDesc(itemsIds,
                                BookingStatus.REJECTED.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case ALL:
            default:
                return bookingRepository.findByItem_idInOrderByStartDesc(itemsIds).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }
}
