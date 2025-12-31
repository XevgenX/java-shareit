package ru.practicum.shareit.user.domain.repo;

import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.user.domain.model.User;

public interface UserRepo extends CrudRepo<User> {
    boolean isEmailAlreadyExists(String email);
}
