package ru.practicum.shareit.booking.domain;

import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.common.domain.service.CrudService;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface BookingService extends CrudService<Booking> {
    Booking approve(Long id, User user, boolean approved);

    List<Booking> findByBooker(User user, Optional<BookingStatus> status);

    List<Booking> findByOwnerShip(User user, Optional<BookingStatus> status);

    List<Booking> findByItemId(Long itemId);
}
