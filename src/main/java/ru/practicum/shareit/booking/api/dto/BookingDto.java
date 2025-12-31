package ru.practicum.shareit.booking.api.dto;

import lombok.Builder;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.user.api.dto.UserDto;

import java.time.LocalDateTime;

@Builder
public record BookingDto(Long id, LocalDateTime start, LocalDateTime end,
                         ItemDto item, UserDto booker, BookingStatus status,
                         LocalDateTime created) { }
