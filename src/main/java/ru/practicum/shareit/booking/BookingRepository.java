package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking b " +
            "inner join b.booker u " +
            "where u.id = :bookerId " +
            "and :currentDate between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndCurrentOrderByStartDesc(@Param("bookerId") Long bookerId,
                                                                    @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "inner join b.booker u " +
            "where u.id = :bookerId " +
            "and :currentDate > b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndPastOrderByStartDesc(@Param("bookerId") Long bookerId,
                                                                 @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "inner join b.booker u " +
            "where u.id = :bookerId " +
            "and :currentDate < b.start " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndFutureOrderByStartDesc(@Param("bookerId") Long bookerId,
                                                                   @Param("currentDate") LocalDateTime currentDate);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select b from Booking b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    @Query("select b from Booking b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and :currentDate between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndCurrentOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                   @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and :currentDate > b.end " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndPastOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and :currentDate < b.start " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndFutureOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                  @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                  @Param("status") BookingStatus status);
}
