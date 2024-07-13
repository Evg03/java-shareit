package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<CommentDto> findByItem_idOrderByCreatedDesc(int itemId);
}
