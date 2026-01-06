package ru.practicum.shareit.booking.api.dto;

import java.time.LocalDateTime;

public record NewBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
}
