package ru.practicum.shareit.comment.api.dto;

import lombok.Builder;
import ru.practicum.shareit.item.api.dto.ItemDto;

import java.time.LocalDateTime;

@Builder
public record CommentDto(Long id, String text, ItemDto item, String authorName, LocalDateTime created) { }
