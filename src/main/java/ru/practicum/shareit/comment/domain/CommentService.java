package ru.practicum.shareit.comment.domain;

import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.common.domain.service.CrudService;

import java.util.List;

public interface CommentService extends CrudService<Comment> {
    List<Comment> findByItemId(Long itemId);
}
