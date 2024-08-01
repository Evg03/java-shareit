package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    public void addItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .comments(new ArrayList<>())
                .build();
        when(itemService.addItem(any(ItemDto.class), anyInt())).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastBooking", Matchers.is(itemDto.getLastBooking())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextBooking", Matchers.is(itemDto.getNextBooking())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comments", Matchers.is(itemDto.getComments())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.is(itemDto.getRequestId()))
                );
        verify(itemService, times(1)).addItem(any(ItemDto.class), anyInt());
    }

    @Test
    public void addComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("test comment")
                .authorName("name")
                .created(LocalDateTime.now())
                .build();
        CommentCreateDto commentCreateDto = new CommentCreateDto("test comment");
        when(itemService.addComment(anyInt(), anyInt(), any(CommentCreateDto.class))).thenReturn(commentDto);
        mvc.perform(post("/items/" + 1 + "/comment")
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(commentDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is(commentDto.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName", Matchers.is(commentDto.getAuthorName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created", Matchers.is(commentDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)))
                );
        verify(itemService, times(1)).addComment(anyInt(), anyInt(), any(CommentCreateDto.class));
    }

    @Test
    public void updateItem() throws Exception {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("newName")
                .description("newDescription")
                .available(false)
                .build();
        ItemDto updatedItem = ItemDto.builder()
                .id(1)
                .name("newName")
                .description("newDescription")
                .available(false)
                .comments(new ArrayList<>())
                .build();
        when(itemService.updateItem(any(ItemUpdateDto.class), anyInt(), anyInt())).thenReturn(updatedItem);
        mvc.perform(patch("/items/" + 1)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(updatedItem.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(updatedItem.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(updatedItem.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(updatedItem.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastBooking", Matchers.is(updatedItem.getLastBooking())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextBooking", Matchers.is(updatedItem.getNextBooking())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comments", Matchers.is(updatedItem.getComments())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.is(updatedItem.getRequestId()))
                );
        verify(itemService, times(1)).updateItem(any(ItemUpdateDto.class), anyInt(), anyInt());
    }

    @Test
    public void getItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .comments(new ArrayList<>())
                .build();
        when(itemService.getItem(anyInt(), anyInt())).thenReturn(itemDto);
        mvc.perform(get("/items/" + 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastBooking", Matchers.is(itemDto.getLastBooking())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextBooking", Matchers.is(itemDto.getNextBooking())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comments", Matchers.is(itemDto.getComments())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.is(itemDto.getRequestId()))
                );
        verify(itemService, times(1)).getItem(anyInt(), anyInt());
    }

    @Test
    public void getItems() throws Exception {
        when(itemService.getItems(anyInt())).thenReturn(new ArrayList<>());
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]")
                );
        verify(itemService, times(1)).getItems(anyInt());
    }

    @Test
    public void searchByText() throws Exception {
        when(itemService.searchByText(anyString())).thenReturn(new ArrayList<>());
        mvc.perform(get("/items/search")
                        .param("text", "test text")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]")
                );
        verify(itemService, times(1)).searchByText(anyString());
    }
}
