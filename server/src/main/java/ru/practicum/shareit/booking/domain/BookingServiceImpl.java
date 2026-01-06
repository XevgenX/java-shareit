package ru.practicum.shareit.booking.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.booking.domain.repo.BookingRepo;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.common.domain.service.CrudServiceImpl;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl extends CrudServiceImpl<Booking> implements BookingService {
    private final BookingRepo repo;

    @Override
    public Booking approve(Long id, User user, boolean approved) {
        Booking booking = repo.findById(id).orElseThrow(() -> new ValidationException("Booking not existed"));
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new ValidationException("Cannot approve item without ownership");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return save(booking);
    }

    @Override
    public List<Booking> findByBooker(User user, Optional<BookingStatus> status) {
        if (status.isPresent()) {
            return repo.findByBookerAndState(user, status.get());
        } else {
            return repo.findByBooker(user);
        }
    }

    @Override
    public List<Booking> findByOwnerShip(User user, Optional<BookingStatus> status) {
        if (status.isPresent()) {
            return repo.findByOwnerShipAndState(user, status.get());
        } else {
            return repo.findByOwnerShip(user);
        }
    }

    @Override
    public List<Booking> findByItemId(Long itemId) {
        return repo.findByItemId(itemId);
    }


    @Override
    protected void validateBeforeCreate(Booking model) {
        commonValidation(model);
    }

    @Override
    protected void validateBeforePatch(Booking model) {
        commonValidation(model);
    }

    @Override
    protected CrudRepo<Booking> getRepo() {
        return repo;
    }

    private void commonValidation(Booking model) {
        if (!model.getItem().getAvailable()) {
            throw new ValidationException("Cannot book unavailable item");
        }
    }
}
