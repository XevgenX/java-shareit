package ru.practicum.shareit.booking.persistence.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.booking.persistence.entity.BookingEntity;

import java.util.List;

public interface BookingRepository extends CrudRepository<BookingEntity, Long> {
    @Query("""
        SELECT b FROM BookingEntity b
        LEFT JOIN FETCH b.booker u
        WHERE u.id = :bookerId AND b.status = :status
    """)
    List<BookingEntity> findByBookerAndState(Long bookerId, BookingStatus status);

    @Query("""
        SELECT b FROM BookingEntity b
        LEFT JOIN FETCH b.booker u
        WHERE u.id = :bookerId
    """)
    List<BookingEntity> findByBooker(Long bookerId);

    @Query("""
        SELECT b FROM BookingEntity b
        LEFT JOIN FETCH b.item i
        LEFT JOIN FETCH i.owner o
        WHERE o.id = :ownerId AND b.status = :status
    """)
    List<BookingEntity> findByOwnerShipAndState(Long ownerId, BookingStatus status);

    @Query("""
        SELECT b FROM BookingEntity b
        LEFT JOIN FETCH b.item i
        LEFT JOIN FETCH i.owner o
        WHERE o.id = :ownerId
    """)
    List<BookingEntity> findByOwnerShip(Long ownerId);

    List<BookingEntity> findByItemId(Long itemId);
}
