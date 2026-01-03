package ru.practicum.shareit.booking.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.NewBookingDto;
import ru.practicum.shareit.booking.api.mapper.BookingApiMapper;
import ru.practicum.shareit.booking.domain.BookingService;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.item.domain.ItemService;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingApiMapper mapper;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody @Valid NewBookingDto dto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        validate(dto);
        Booking model = mapper.toModel(dto);
        Item item = itemService.findById(dto.itemId());
        model.setItem(item);
        User user = userService.findById(userId);
        model.setBooker(user);
        Booking created = bookingService.save(model);
        created.setItem(item);
        created.setBooker(user);
        return ResponseEntity
                .created(URI.create("/bookings/" + created.getId()))
                .body(mapper.toDto(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingDto> approve(@PathVariable long id,
                                              @RequestParam boolean approved,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        User user = null;
        try {
            user = userService.findById(userId);
        } catch (NotFoundException e) {
            throw new ValidationException("User not found");
        }
        BookingDto saved = mapper.toDto(bookingService.approve(id, user, approved));
        return ResponseEntity
                .ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> findById(@PathVariable long id) {
        return ResponseEntity.ok(mapper.toDto(bookingService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(required = false) Optional<BookingStatus> status) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(mapper.toDtos(bookingService.findByBooker(user, status)));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> findByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @RequestParam(required = false) Optional<BookingStatus> status) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(mapper.toDtos(bookingService.findByOwnerShip(user, status)));
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
