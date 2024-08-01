package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @MockBean
    private RequestService requestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    public void addRequest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description");
        when(requestService.addRequest(any(ItemRequestCreateDto.class), anyInt())).thenReturn(requestDto);
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(requestDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(requestDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created", Matchers.is(requestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.is(requestDto.getItems()))
                );
        verify(requestService, times(1)).addRequest(any(ItemRequestCreateDto.class), anyInt());
    }

    @Test
    public void getRequests() throws Exception {
        when(requestService.getRequests(anyInt())).thenReturn(new ArrayList<>());
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]")
                );
        verify(requestService, times(1)).getRequests(anyInt());
    }

    @Test
    public void getAllRequests() throws Exception {
        when(requestService.getAllRequests(anyInt(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]")
                );
        verify(requestService, times(1)).getAllRequests(anyInt(), anyInt(), anyInt());
    }

    @Test
    public void getRequestById() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        when(requestService.getRequestById(anyInt(), anyInt())).thenReturn(requestDto);
        mvc.perform(get("/requests/" + 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(requestDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(requestDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created", Matchers.is(requestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.is(requestDto.getItems()))
                );
        verify(requestService, times(1)).getRequestById(anyInt(), anyInt());
    }
}
