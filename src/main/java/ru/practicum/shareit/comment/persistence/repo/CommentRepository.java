package ru.practicum.shareit.comment.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comment.persistence.entity.CommentEntity;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<CommentEntity, Long> {
    List<CommentEntity> findByItemId(Long itemId);
}
