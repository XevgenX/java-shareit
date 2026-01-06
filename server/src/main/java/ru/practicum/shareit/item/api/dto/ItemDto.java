package ru.practicum.shareit.item.api.dto;

import lombok.*;

@Builder
public record ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
}
