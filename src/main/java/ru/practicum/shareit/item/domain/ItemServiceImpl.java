package ru.practicum.shareit.item.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.common.domain.service.CrudServiceImpl;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.domain.repo.ItemRepo;
import ru.practicum.shareit.user.domain.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends CrudServiceImpl<Item> implements ItemService {
    private final ItemRepo repo;

    @Override
    public List<Item> findByOwner(User user) {
        validate(user);
        return repo.findByOwner(user);
    }

    @Override
    public List<Item> findByTextContainsInNameAndDescription(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return Collections.emptyList();
        }
        return repo.findByTextContainsInNameAndDescription(text);
    }

    private void validate(User user) {
        if (Objects.isNull(user) || Objects.isNull(user.getId())) {
            throw new ValidationException("Некорректный user");
        }
    }

    @Override
    protected void validateBeforeCreate(Item model) {
        if (Objects.isNull(model.getAvailable())
                || Objects.isNull(model.getName()) || model.getName().isBlank()
                || Objects.isNull(model.getDescription()) || model.getDescription().isBlank()) {
            throw new ValidationException("Данные не корректны");
        }
    }

    @Override
    protected void validateBeforePatch(Item model) {

    }

    @Override
    protected CrudRepo<Item> getRepo() {
        return repo;
    }
}
