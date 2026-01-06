package ru.practicum.shareit.request.api.dto;

import lombok.Builder;

@Builder
public record NewRequestDto(String description) {
}
