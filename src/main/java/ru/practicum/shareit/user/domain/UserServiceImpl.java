package ru.practicum.shareit.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.domain.exception.DataConflictException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.common.domain.service.CrudServiceImpl;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.domain.repo.UserRepo;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CrudServiceImpl<User> implements UserService {
    private final UserRepo repo;

    @Override
    protected CrudRepo<User> getRepo() {
        return repo;
    }

    @Override
    protected void validateBeforeCreate(User user) {
        if (Objects.isNull(user.getEmail())
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            throw new ValidationException("Почта не корректна");
        }
        if (repo.isEmailAlreadyExists(user.getEmail())) {
            throw new DataConflictException("Пользователь с такой почтой уже существует");
        }
    }

    @Override
    protected void validateBeforePatch(User user) {
        if (Objects.nonNull(user.getEmail())) {
            if (user.getEmail().isBlank()
                    || !user.getEmail().contains("@")) {
                throw new ValidationException("Почта не корректна");
            }
            if (repo.isEmailAlreadyExists(user.getEmail())) {
                throw new DataConflictException("Пользователь с такой почтой уже существует");
            }
        }
    }
}
