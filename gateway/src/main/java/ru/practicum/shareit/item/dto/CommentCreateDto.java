package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class CommentCreateDto {
    @NotBlank(message = "Комментарий не должен быть пустым или null")
    private String text;
}
