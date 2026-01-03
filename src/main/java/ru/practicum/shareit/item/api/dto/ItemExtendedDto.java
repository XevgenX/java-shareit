package ru.practicum.shareit.item.api.dto;

import lombok.Builder;
import ru.practicum.shareit.comment.api.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ItemExtendedDto(Long id, String name, String description, Boolean available, LocalDateTime lastBooking, LocalDateTime nextBooking, List<CommentDto> comments) {
}
