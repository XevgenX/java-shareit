package ru.practicum.shareit.comment.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.comment.persistence.entity.CommentEntity;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;
import ru.practicum.shareit.item.persistence.mapper.ItemPersistenceMapper;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.entity.UserEntity;
import ru.practicum.shareit.user.persistence.mapper.UserPersistenceMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentPersistenceMapper {
    private final UserPersistenceMapper userMapper;
    private final ItemPersistenceMapper itemMapper;

    public Comment toDomain(CommentEntity entity) {
        if (entity == null) {
            return null;
        }

        Item item = null;
        if (entity.getItem() != null) {
            item = itemMapper.toDomain(entity.getItem());
        }

        User booker = null;
        if (entity.getAuthor() != null) {
            booker = userMapper.toDomain(entity.getAuthor());
        }

        return Comment.builder()
                .id(entity.getId())
                .item(item)
                .author(booker)
                .text(entity.getText())
                .created(entity.getCreated())
                .build();
    }

    public CommentEntity toEntity(Comment domain) {
        if (domain == null) {
            return null;
        }

        ItemEntity itemEntity = null;
        if (domain.getItem() != null) {
            itemEntity = ItemEntity.builder()
                    .id(domain.getItem().getId())
                    .build();
        }

        UserEntity bookerEntity = null;
        if (domain.getAuthor() != null) {
            bookerEntity = UserEntity.builder()
                    .id(domain.getAuthor().getId())
                    .build();
        }

        return CommentEntity.builder()
                .id(domain.getId())
                .item(itemEntity)
                .author(bookerEntity)
                .text(domain.getText())
                .created(domain.getCreated())
                .build();
    }

    public void updateEntityFromDomain(Comment domain, CommentEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        if (domain.getText() != null) {
            entity.setText(domain.getText());
        }
    }

    public List<Comment> toDomainList(List<CommentEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
}
