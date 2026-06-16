package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime dateTime);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime dateTime);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findItemBookingsByOwnerId(long ownerId);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findItemBookingsByOwnerIdAndStatus(long bookerId, BookingStatus status);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentItemBookingsByOwnerId(long bookerId, LocalDateTime dateTime);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findPastItemBookingsByOwnerId(long bookerId, LocalDateTime dateTime);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findFutureItemBookingsByOwnerId(long bookerId, LocalDateTime dateTime);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime dateTime);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime dateTime);

    boolean existsByBookerIdAndItemIdAndEndIsBefore(long bookerId, long itemId, LocalDateTime dateTime);
}
