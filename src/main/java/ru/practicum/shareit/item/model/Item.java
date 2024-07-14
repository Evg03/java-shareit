package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Имя не должно быть пустым или null")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым или null")
    private String description;
    @NotNull(message = "Статус вещи не может быть null")
    @Column(name = "is_available")
    private boolean available;
    @NotNull
    @Column(name = "owner_id")
    private Integer owner;
    @Column(name = "request_id")
    private Integer request;
}
