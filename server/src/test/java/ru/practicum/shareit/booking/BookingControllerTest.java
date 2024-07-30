package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private final User user = new User(2, "name", "test@mail.ru");
    private final Item item = new Item(
            1,
            "name",
            "description",
            true,
            2,
            null
    );
    private final BookingDto bookingDto = new BookingDto(
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1),
            item,
            user,
            "WAITING"
    );
    private final BookingDto approvedBookingDto = new BookingDto(
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1),
            item,
            user,
            "APPROVED"
    );
    private final BookingCreateDto bookingCreateDto = new BookingCreateDto(
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1)
    );

    @Test
    public void addBooking() throws Exception {
        when(bookingService.addBooking(any(BookingCreateDto.class), anyInt())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item", Matchers.is(bookingDto.getItem()), Item.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker", Matchers.is(bookingDto.getBooker()), User.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(bookingDto.getStatus()))
                );
        verify(bookingService, times(1)).addBooking(any(BookingCreateDto.class), anyInt());
    }

    @Test
    public void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(approvedBookingDto);
        mvc.perform(patch("/bookings/" + 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(approvedBookingDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(approvedBookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(approvedBookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item", Matchers.is(approvedBookingDto.getItem()), Item.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker", Matchers.is(approvedBookingDto.getBooker()), User.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(approvedBookingDto.getStatus()))
                );
        verify(bookingService, times(1)).approveBooking(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    public void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyInt(), anyInt())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/" + 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item", Matchers.is(bookingDto.getItem()), Item.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker", Matchers.is(bookingDto.getBooker()), User.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(bookingDto.getStatus()))
                );
        verify(bookingService, times(1)).getBookingById(anyInt(), anyInt());
    }

    @Test
    public void getAllUserBookings() throws Exception {
        when(bookingService.getAllUserBookings(anyString(), anyInt())).thenReturn(new ArrayList<>());
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", State.ALL.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]")
                );
        verify(bookingService, times(1)).getAllUserBookings(anyString(), anyInt());
    }

    @Test
    public void getAllUserItemsBookings() throws Exception {
        when(bookingService.getAllUserItemsBookings(anyString(), anyInt())).thenReturn(new ArrayList<>());
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", State.ALL.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]")
                );
        verify(bookingService, times(1)).getAllUserItemsBookings(anyString(), anyInt());
    }

}
