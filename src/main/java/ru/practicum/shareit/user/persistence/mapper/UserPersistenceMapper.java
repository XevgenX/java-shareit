package ru.practicum.shareit.user.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.entity.UserEntity;

import java.util.Objects;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .build();
    }

    public void updateEntityFromDomain(User domain, UserEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        if (Objects.nonNull(domain.getName()) && !domain.getName().isBlank()) {
            entity.setName(domain.getName());
        }
        if (Objects.nonNull(domain.getEmail()) && !domain.getEmail().isBlank()) {
            entity.setEmail(domain.getEmail());
        }
    }

    public UserEntity toNewEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .name(domain.getName())
                .email(domain.getEmail())
                .build();
    }
}
