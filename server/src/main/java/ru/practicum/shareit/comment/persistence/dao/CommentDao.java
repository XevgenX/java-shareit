package ru.practicum.shareit.comment.persistence.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.comment.domain.repo.CommentRepo;
import ru.practicum.shareit.comment.persistence.entity.CommentEntity;
import ru.practicum.shareit.comment.persistence.mapper.CommentPersistenceMapper;
import ru.practicum.shareit.comment.persistence.repo.CommentRepository;
import ru.practicum.shareit.common.domain.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentDao implements CommentRepo {
    private final CommentRepository repository;
    private final CommentPersistenceMapper mapper;


    @Override
    public Optional<Comment> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Comment create(Comment comment) {
        return mapper.toDomain(repository.save(mapper.toEntity(comment)));
    }

    @Override
    public Comment update(Comment comment) {
        CommentEntity entity = repository.findById(comment.getId())
                .orElseThrow(() -> new NotFoundException("user not found"));
        mapper.updateEntityFromDomain(comment, entity);
        return mapper.toDomain(
                repository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Comment> findByItemId(Long itemId) {
        return mapper.toDomainList(repository.findByItemId(itemId));
    }
}
