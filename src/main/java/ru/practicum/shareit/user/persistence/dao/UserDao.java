package ru.practicum.shareit.user.persistence.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.domain.repo.UserRepo;
import ru.practicum.shareit.user.persistence.entity.UserEntity;
import ru.practicum.shareit.user.persistence.mapper.UserPersistenceMapper;
import ru.practicum.shareit.user.persistence.repo.UserRepository;

import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class UserDao implements UserRepo {
    private final UserRepository repository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public User create(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public User update(User user) {
        UserEntity entity = repository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("user not found"));
        mapper.updateEntityFromDomain(user, entity);
        return mapper.toDomain(
                repository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean isEmailAlreadyExists(String email) {
        return repository.existsByEmail(email);
    }
}
