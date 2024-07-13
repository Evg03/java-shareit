package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBooker_idOrderByStartDesc(int bookerId);

    List<Booking> findByBooker_idAndStatusOrderByStartDesc(int bookerId, String status);

    List<Booking> findByBooker_idAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime date);

    List<Booking> findByBooker_idAndEndBeforeOrderByStartDesc(int bookerId, LocalDateTime date);

    List<Booking> findByBooker_idAndStartBeforeAndEndAfterOrderByBooker_idDesc(int bookerId, LocalDateTime date1, LocalDateTime date2);

    List<Booking> findByItem_idInOrderByStartDesc(Collection<Integer> itemsIds);

    List<Booking> findByItem_idInAndStatusOrderByStartDesc(Collection<Integer> itemsIds, String status);

    List<Booking> findByItem_idInAndStartAfterOrderByStartDesc(Collection<Integer> itemsIds, LocalDateTime date);

    List<Booking> findByItem_idInAndEndBeforeOrderByStartDesc(Collection<Integer> itemsIds, LocalDateTime date);

    List<Booking> findByItem_idInAndStartBeforeAndEndAfterOrderByStartDesc(Collection<Integer> itemsIds, LocalDateTime date1, LocalDateTime date2);

    List<Booking> findByItem_idAndStartAfterAndStatusNotOrderByStartAsc(int itemId, LocalDateTime date, String status);

    List<Booking> findByItem_idAndStartBeforeOrderByStartDesc(int itemId, LocalDateTime date);

    List<Booking> findByItem_idAndBooker_idAndEndBeforeAndStatusNot(int itemId, int userId, LocalDateTime date, String status);
}
