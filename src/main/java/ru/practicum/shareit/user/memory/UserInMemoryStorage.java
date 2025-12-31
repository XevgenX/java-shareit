package ru.practicum.shareit.user.memory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.memory.CrudInMemoryStorage;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.domain.repo.UserRepo;

@Repository
public class UserInMemoryStorage extends CrudInMemoryStorage<User> implements UserRepo {
    @Override
    protected User createCopy(User model, Long newId) {
        return User.builder()
                .id(newId)
                .name(model.getName())
                .email(model.getEmail())
                .build();
    }

    @Override
    protected User createCopyWithPartialUpdate(User newModel, User existingModel) {
        return User.builder()
                .id(existingModel.getId())
                .name(newModel.getName() != null ? newModel.getName() : existingModel.getName())
                .email(newModel.getEmail() != null ? newModel.getEmail() : existingModel.getEmail())
                .build();
    }

    @Override
    public boolean isEmailAlreadyExists(String email) {
        return items.values().stream().map(User::getEmail)
                .anyMatch(e -> e.equalsIgnoreCase(email));
    }
}
