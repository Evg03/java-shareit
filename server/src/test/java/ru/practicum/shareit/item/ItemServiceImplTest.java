package ru.practicum.shareit.item;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.IncorrectItemOwnerIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemNotRentedByUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "test@mail.ru");
        item = new Item(
                1,
                "name",
                "description",
                true,
                1,
                null
        );
    }

    @Test
    public void addItem() {
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        ItemDto itemDto = new ItemDto(
                1,
                "name",
                "description",
                true,
                null,
                null,
                null,
                null
        );
        ItemDto addedItem = itemService.addItem(itemDto, 1);
        Assertions.assertNotNull(addedItem);
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    public void addItemUserNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        ItemDto itemDto = new ItemDto(
                1,
                "name",
                "description",
                true,
                null,
                null,
                null,
                null
        );
        Assert.assertThrows(UserNotFoundException.class, () -> itemService.addItem(itemDto, 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void updateItem() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "name",
                "description",
                true
        );
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        ItemDto itemDto = itemService.updateItem(itemUpdateDto, 1, 1);
        Assertions.assertNotNull(itemDto);
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    public void updateItemUserNotExist() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "name",
                "description",
                true
        );
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assert.assertThrows(UserNotFoundException.class, () -> itemService.updateItem(itemUpdateDto, 1, 1));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void updateItemNotExist() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "name",
                "description",
                true
        );
        when(itemRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(itemUpdateDto, 1, 1));
    }

    @Test
    public void updateItemWrongItemOwner() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "name",
                "description",
                true
        );
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        Assert.assertThrows(IncorrectItemOwnerIdException.class, () -> itemService.updateItem(itemUpdateDto, 2, 1));
    }

    @Test
    public void getItem() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_idAndStartBeforeOrderByStartDesc(anyInt(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItem_idAndStartAfterAndStatusNotOrderByStartAsc(anyInt(), any(), anyString())).thenReturn(new ArrayList<>());
        when(commentRepository.findByItem_idOrderByCreatedDesc(anyInt())).thenReturn(new ArrayList<>());
        ItemDto itemDto = itemService.getItem(1, 1);
        Assertions.assertNotNull(itemDto);
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByItem_idAndStartBeforeOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, times(1)).findByItem_idAndStartAfterAndStatusNotOrderByStartAsc(anyInt(), any(), anyString());
        verify(commentRepository, times(1)).findByItem_idOrderByCreatedDesc(anyInt());
    }

    @Test
    public void getItemNotExist() {
        when(itemRepository.findById(1)).thenReturn(Optional.empty());
        Assert.assertThrows(ItemNotFoundException.class, () -> itemService.getItem(1, 1));
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    public void getItemsEmpty() {
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<ItemDto> items = itemService.getItems(1);
        Assertions.assertTrue(items.isEmpty());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
    }

    @Test
    public void getItems() {
        Item item2 = new Item(
                2,
                "name2",
                "description2",
                true,
                1,
                null
        );
        List<Item> itemsList = List.of(item, item2);
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt())).thenReturn(itemsList);
        when(bookingRepository.findByItem_idAndStartBeforeOrderByStartDesc(anyInt(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItem_idAndStartAfterAndStatusNotOrderByStartAsc(anyInt(), any(), anyString())).thenReturn(new ArrayList<>());
        when(commentRepository.findByItem_idOrderByCreatedDesc(anyInt())).thenReturn(new ArrayList<>());
        List<ItemDto> items = itemService.getItems(1);
        Assertions.assertFalse(items.isEmpty());
        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyInt());
        verify(bookingRepository, times(itemsList.size())).findByItem_idAndStartBeforeOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, times(itemsList.size())).findByItem_idAndStartAfterAndStatusNotOrderByStartAsc(anyInt(), any(), anyString());
        verify(commentRepository, times(itemsList.size())).findByItem_idOrderByCreatedDesc(anyInt());
    }

    @Test
    public void searchByText() {
        when(itemRepository.findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue(anyString(), anyString())).thenReturn(new ArrayList<>());
        List<ItemDto> items = itemService.searchByText("text");
        Assertions.assertTrue(items.isEmpty());
        verify(itemRepository, times(1)).findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue(anyString(), anyString());
    }

    @Test
    public void addComment() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idAndBooker_idAndEndBeforeAndStatusNot(anyInt(),
                anyInt(),
                any(),
                any())).thenReturn(new ArrayList<>(List.of(new Booking())));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("text");
        CommentDto commentDto = itemService.addComment(1, 1, commentCreateDto);
        Assertions.assertNotNull(commentDto);
        verify(itemRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).findById(anyInt());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    public void addCommentItemNotExist() {
        when(itemRepository.findById(1)).thenReturn(Optional.empty());
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("text");
        Assert.assertThrows(ItemNotFoundException.class, () -> itemService.addComment(1, 1, commentCreateDto));
        verify(itemRepository, times(1)).findById(anyInt());
    }

    @Test
    public void addCommentUserNotExist() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("text");
        Assert.assertThrows(UserNotFoundException.class, () -> itemService.addComment(1, 1, commentCreateDto));
        verify(itemRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    public void addCommentItemNotRentedByUser() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_idAndBooker_idAndEndBeforeAndStatusNot(anyInt(),
                anyInt(),
                any(),
                any())).thenReturn(new ArrayList<>());
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("text");
        Assert.assertThrows(ItemNotRentedByUserException.class, () -> itemService.addComment(1, 1, commentCreateDto));
        verify(itemRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_idAndBooker_idAndEndBeforeAndStatusNot(anyInt(),
                anyInt(),
                any(),
                any());
    }
}
