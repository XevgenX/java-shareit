package ru.practicum.shareit.user.api.dto;

import lombok.Builder;

@Builder
public record UserDto(Long id, String name, String email) { }