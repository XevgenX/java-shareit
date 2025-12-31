package ru.practicum.shareit.booking.domain.repo;

import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;

public interface BookingRepo extends CrudRepo<Booking> {
    List<Booking> findByBookerAndState(User owner, BookingStatus status);

    List<Booking> findByBooker(User owner);

    List<Booking> findByOwnerShipAndState(User owner, BookingStatus status);

    List<Booking> findByOwnerShip(User owner);

    List<Booking> findByItemId(Long itemId);
}
