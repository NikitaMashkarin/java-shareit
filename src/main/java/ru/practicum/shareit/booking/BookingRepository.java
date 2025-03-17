package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from BookingEntity b " +
            "inner join b.booker u " +
            "where u.id = :bookerId " +
            "and :currentDate between b.start and b.end " +
            "order by b.start desc")
    List<BookingEntity> findAllByBookerIdAndCurrentOrderByStartDesc(@Param("bookerId") Long bookerId,
                                                                    @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from BookingEntity b " +
            "inner join b.booker u " +
            "where u.id = :bookerId " +
            "and :currentDate > b.end " +
            "order by b.start desc")
    List<BookingEntity> findAllByBookerIdAndPastOrderByStartDesc(@Param("bookerId") Long bookerId,
                                                                 @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from BookingEntity b " +
            "inner join b.booker u " +
            "where u.id = :bookerId " +
            "and :currentDate < b.start " +
            "order by b.start desc")
    List<BookingEntity> findAllByBookerIdAndFutureOrderByStartDesc(@Param("bookerId") Long bookerId,
                                                                   @Param("currentDate") LocalDateTime currentDate);

    List<BookingEntity> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select b from BookingEntity b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "order by b.start desc")
    List<BookingEntity> findAllByOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    @Query("select b from BookingEntity b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and :currentDate between b.start and b.end " +
            "order by b.start desc")
    List<BookingEntity> findAllByOwnerIdAndCurrentOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                   @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from BookingEntity b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and :currentDate > b.end " +
            "order by b.start desc")
    List<BookingEntity> findAllByOwnerIdAndPastOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from BookingEntity b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and :currentDate < b.start " +
            "order by b.start desc")
    List<BookingEntity> findAllByOwnerIdAndFutureOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                  @Param("currentDate") LocalDateTime currentDate);

    @Query("select b from BookingEntity b " +
            "inner join b.item t " +
            "inner join t.owner u " +
            "where u.id = :ownerId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<BookingEntity> findAllByOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                  @Param("status") BookingStatus status);
}