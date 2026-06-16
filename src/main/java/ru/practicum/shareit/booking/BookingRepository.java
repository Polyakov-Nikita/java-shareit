package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime startDateTime, LocalDateTime endDateTime, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(long bookerId, LocalDateTime dateTime, Sort sort);

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

    Booking findFirstByItemIdAndEndBefore(long itemId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id in ?1 " +
            "and b.end < ?2 " +
            "order by b.end desc")
    List<Booking> findLastBookingsForItems(List<Long> itemIds, LocalDateTime dateTime);

    Booking findFirstByItemIdAndStartAfter(long itemId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id in ?1 " +
            "and b.start > ?2 " +
            "order by b.start asc")
    List<Booking> findNextBookingsForItems(List<Long> itemIds, LocalDateTime dateTime);

    boolean existsByBookerIdAndItemIdAndEndIsBefore(long bookerId, long itemId, LocalDateTime dateTime);
}
