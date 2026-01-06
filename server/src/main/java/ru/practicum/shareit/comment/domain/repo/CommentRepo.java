package ru.practicum.shareit.comment.domain.repo;

import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.common.domain.repo.CrudRepo;

import java.util.List;

public interface CommentRepo extends CrudRepo<Comment> {
    List<Comment> findByItemId(Long itemId);
}
