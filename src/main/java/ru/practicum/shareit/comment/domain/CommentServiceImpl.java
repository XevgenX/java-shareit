package ru.practicum.shareit.comment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.domain.BookingService;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.comment.domain.repo.CommentRepo;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.common.domain.service.CrudServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends CrudServiceImpl<Comment> implements CommentService {
    private final CommentRepo repo;
    private final BookingService bookingService;

    @Override
    public List<Comment> findByItemId(Long itemId) {
        return repo.findByItemId(itemId);
    }

    @Override
    protected void validateBeforeCreate(Comment model) {
        List<Booking> bookings = bookingService.findByBooker(model.getAuthor(), Optional.empty());
        Optional<Booking> bookedItem = bookings.stream()
                .filter(booked -> booked.getItem().getId().equals(model.getItem().getId()))
                .findAny();
        if (bookedItem.isEmpty() || bookedItem.get().getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Comments can be placed only for booked items");
        }
    }

    @Override
    protected void validateBeforePatch(Comment model) {

    }

    @Override
    protected CrudRepo<Comment> getRepo() {
        return repo;
    }


}
