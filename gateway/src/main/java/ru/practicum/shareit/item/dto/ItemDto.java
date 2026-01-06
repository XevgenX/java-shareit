package ru.practicum.shareit.item.dto;

import lombok.Builder;

@Builder
public record ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
}
