package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(int userId);

    List<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(int userId, Pageable pageable);
}
