package ru.practicum.shareit.booking;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        user = new User(2, "name", "test@mail.ru");
        item = new Item(
                1,
                "name",
                "description",
                true,
                2,
                null
        );
        booking = new Booking(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                item,
                user,
                "WAITING"
        );
        bookingCreateDto = new BookingCreateDto(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
    }

    @Test
    public void addBooking() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BookingDto bookingDto = bookingService.addBooking(bookingCreateDto, 1);
        Assertions.assertNotNull(bookingDto);
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    public void addBookingUserNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(bookingCreateDto, 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void addBookingItemNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(ItemNotFoundException.class, () -> bookingService.addBooking(bookingCreateDto, 1));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void addBookingOwnItem() {
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assert.assertThrows(BookingOwnItemException.class, () -> bookingService.addBooking(bookingCreateDto, 2));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void addBookingItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assert.assertThrows(ItemNotAvailableException.class, () -> bookingService.addBooking(bookingCreateDto, 1));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void addBookingStartIsNull() {
        bookingCreateDto = new BookingCreateDto(
                1,
                null,
                LocalDateTime.now()
        );
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assert.assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(bookingCreateDto, 1));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void addBookingEndIsNull() {
        bookingCreateDto = new BookingCreateDto(
                1,
                LocalDateTime.now(),
                null
        );
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assert.assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(bookingCreateDto, 1));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void addBookingStartIsEqualEnd() {
        LocalDateTime date = LocalDateTime.now();
        bookingCreateDto = new BookingCreateDto(
                1,
                date,
                date
        );
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assert.assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(bookingCreateDto, 1));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void addBookingStartIsAfterEnd() {
        bookingCreateDto = new BookingCreateDto(
                1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now()
        );
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assert.assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(bookingCreateDto, 1));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void approveBooking() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BookingDto bookingDto = bookingService.approveBooking(1, 2, true);
        Assertions.assertNotNull(bookingDto);
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    public void rejectBooking() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BookingDto bookingDto = bookingService.approveBooking(1, 2, false);
        Assertions.assertNotNull(bookingDto);
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    public void approveBookingNotExist() {
        when(bookingRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(BookingNotFoundException.class, () -> bookingService.approveBooking(1, 2, true));
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    public void approveBookingIncorrectItemOwner() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        Assert.assertThrows(IncorrectBookingOwnerIdException.class, () -> bookingService.approveBooking(1, 1, true));
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    public void approveBookingStatusAlreadyChanged() {
        booking = new Booking(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                item,
                user,
                "APPROVED"
        );
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        Assert.assertThrows(BookingStatusAlreadyChangedException.class, () -> bookingService.approveBooking(1, 2, true));
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    public void getBookingById() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        BookingDto bookingDto = bookingService.getBookingById(1, 2);
        Assertions.assertNotNull(bookingDto);
        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getBookingByIdNotExist() {
        when(bookingRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(1, 2));
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    public void getBookingByIdUserNotExist() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        Assert.assertThrows(IncorrectItemOwnerIdException.class, () -> bookingService.getBookingById(1, 2));
        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getBookingByIdIncorrectOwner() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        Assert.assertThrows(IncorrectBookingOwnerIdException.class, () -> bookingService.getBookingById(1, 1));
        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsByCurrentState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_idAndStartBeforeAndEndAfterOrderByBooker_idDesc(anyInt(),
                any(), any())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserBookings("CURRENT", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_idAndStartBeforeAndEndAfterOrderByBooker_idDesc(anyInt(),
                any(), any());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsByPastState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(anyInt(), any())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserBookings("PAST", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_idAndEndBeforeOrderByStartDesc(anyInt(), any());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsByFutureState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_idAndStartAfterOrderByStartDesc(anyInt(), any())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserBookings("FUTURE", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_idAndStartAfterOrderByStartDesc(anyInt(), any());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsByWaitingState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_idAndStatusOrderByStartDesc(anyInt(), anyString())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserBookings("WAITING", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_idAndStatusOrderByStartDesc(anyInt(), anyString());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsByRejectedState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_idAndStatusOrderByStartDesc(anyInt(), anyString())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserBookings("REJECTED", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_idAndStatusOrderByStartDesc(anyInt(), anyString());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsByAllState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_idOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserBookings("ALL", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_idOrderByStartDesc(anyInt());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsByUnknownState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        Assert.assertThrows(UnknownStateException.class, () -> bookingService.getAllUserBookings("UNKNOWN", 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserBookingsUserNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> bookingService.getAllUserBookings("CURRENT", 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserItemsBookingsByCurrentState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idInAndStartBeforeAndEndAfterOrderByStartDesc(any(),
                any(), any())).thenReturn(new ArrayList<>());
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserItemsBookings("CURRENT", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByItem_idInAndStartBeforeAndEndAfterOrderByStartDesc(any(),
                any(), any());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
    }

    @Test
    public void getAllUserItemsBookingsByPastState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idInAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(new ArrayList<>());
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserItemsBookings("PAST", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByItem_idInAndEndBeforeOrderByStartDesc(any(), any());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
    }

    @Test
    public void getAllUserItemsBookingsByFutureState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idInAndStartAfterOrderByStartDesc(any(), any())).thenReturn(new ArrayList<>());
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserItemsBookings("FUTURE", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByItem_idInAndStartAfterOrderByStartDesc(any(), any());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
    }

    @Test
    public void getAllUserItemsBookingsByWaitingState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idInAndStatusOrderByStartDesc(any(), anyString())).thenReturn(new ArrayList<>());
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserItemsBookings("WAITING", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByItem_idInAndStatusOrderByStartDesc(any(), anyString());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
    }

    @Test
    public void getAllUserItemsBookingsByRejectedState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idInAndStatusOrderByStartDesc(any(), anyString())).thenReturn(new ArrayList<>());
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserItemsBookings("REJECTED", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByItem_idInAndStatusOrderByStartDesc(any(), anyString());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
    }

    @Test
    public void getAllUserItemsBookingsByAllState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idInOrderByStartDesc(any())).thenReturn(new ArrayList<>());
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingDto> bookings = bookingService.getAllUserItemsBookings("ALL", 1);
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByItem_idInOrderByStartDesc(any());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
    }

    @Test
    public void getAllUserItemsBookingsByUnknownState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        Assert.assertThrows(UnknownStateException.class, () -> bookingService.getAllUserItemsBookings("UNKNOWN", 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void getAllUserItemsBookingsUserNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(UserNotFoundException.class, () -> bookingService.getAllUserItemsBookings("CURRENT", 1));
        verify(userRepository, times(1)).findById(any());
    }

}
