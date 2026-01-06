package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.validator.Validator;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient client;
    private final Validator validator;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody NewBookingDto dto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(dto);
        validator.validate(userId);
        validate(dto);
        return client.create(dto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approve(@PathVariable long id,
                                              @RequestParam boolean approved,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(id);
        validator.validate(userId);
        return client.approve(id, approved, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        validator.validate(id);
        return client.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(required = false) Optional<BookingStatus> status) {
        return client.findAll(userId, status);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @RequestParam(required = false) Optional<BookingStatus> status) {
        validator.validate(userId);
        return client.findByOwnerId(userId, status);
    }

    private void validate(NewBookingDto dto) {
        LocalDateTime now = LocalDateTime.now().minusSeconds(10);
        if (dto.start().isBefore(now)) {
            throw new ValidationException("End date cannot be in the Past");
        }
        if (dto.end().isBefore(now)) {
            throw new ValidationException("End date cannot be in the Past");
        }
        if (dto.end().isBefore(dto.start())) {
            throw new ValidationException("End date cannot be before Start");
        }
        if (dto.end().equals(dto.start())) {
            throw new ValidationException("End date cannot equals to Start");
        }
    }
}
