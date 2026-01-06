package ru.practicum.shareit.request.api.dto;

import lombok.Builder;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.user.api.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ItemRequestDto(Long id, String description, UserDto requester,
                             LocalDateTime created, List<ItemDto> items) {
}
