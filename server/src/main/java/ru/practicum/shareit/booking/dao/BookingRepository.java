package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.LastAndNextDate;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
                SELECT
                    b.item.id as itemId,
                    MAX(CASE WHEN b.start < CURRENT_TIMESTAMP THEN b.start ELSE NULL END) as lastBooking,
                    MIN(CASE WHEN b.start > CURRENT_TIMESTAMP THEN b.start ELSE NULL END) as nextBooking
                FROM Booking b
                WHERE b.item.ownerId = ?1
                GROUP BY b.item.id
            """)
    List<LastAndNextDate> findLastAndNextDatesByOwnerId(Long ownerId);

    Boolean existsByItemIdAndBookerIdAndStatusIsAndEndBefore(Long itemId, Long bookerId,
                                                               BookingStatus status, LocalDateTime now);

    // by booker
    List<Booking> findByBookerIdOrderByEndDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusIsOrderByEndDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(Long bookerId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(Long bookerId, LocalDateTime date);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime date);

    // by owner
    List<Booking> findByItemOwnerIdOrderByEndDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusIsOrderByEndDesc(Long ownerId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(Long ownerId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByEndDesc(Long ownerId, LocalDateTime date);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(Long ownerId, LocalDateTime date);
}
