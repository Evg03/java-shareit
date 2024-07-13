package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue(String text1, String text2);

    List<Item> findAllByOwnerOrderByIdAsc(int ownerId);
}
