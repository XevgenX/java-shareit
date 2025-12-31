package ru.practicum.shareit.comment.api.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.api.dto.CommentDto;
import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.item.api.mapper.ItemApiMapper;
import ru.practicum.shareit.user.api.mapper.UserApiMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentApiMapper {
    private final ItemApiMapper itemMapper;
    private final UserApiMapper userMapper;

    public CommentDto toDto(Comment item) {
        if (item == null) {
            return null;
        }
        return CommentDto.builder()
                .id(item.getId())
                .item(itemMapper.toDto(item.getItem()))
                .authorName(userMapper.toDto(item.getAuthor()).name())
                .text(item.getText())
                .build();
    }

    public List<CommentDto> toDtos(List<Comment> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
