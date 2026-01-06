package ru.practicum.shareit.request.dto;

import lombok.Builder;

@Builder
public record NewRequestDto(String description) {
}
