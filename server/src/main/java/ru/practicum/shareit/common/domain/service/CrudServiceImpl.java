package ru.practicum.shareit.common.domain.service;

import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.common.domain.model.Model;
import ru.practicum.shareit.common.domain.repo.CrudRepo;

import java.util.Objects;

public abstract class CrudServiceImpl<M extends Model> implements CrudService<M> {

    @Override
    public M findById(Long id) {
        validate(id);
        return getRepo().findById(id).orElseThrow(() -> new NotFoundException("Не найдено"));
    }

    @Override
    public M save(M item) {
        validate(item);
        if (Objects.nonNull(item.getId())) {
            validateBeforePatch(item);
            return getRepo().update(item);
        } else {
            validateBeforeCreate(item);
            return getRepo().create(item);
        }
    }

    @Override
    public void deleteById(Long id) {
        validate(id);
        getRepo().deleteById(id);
    }

    private void validate(Long id) {
        if (Objects.isNull(id)) {
            throw new ValidationException("Некорректный id");
        }
    }

    protected void validate(M item) {
        if (Objects.isNull(item)) {
            throw new ValidationException("Некорректный item");
        }
    }

    protected abstract void validateBeforeCreate(M model);

    protected abstract void validateBeforePatch(M model);

    protected abstract CrudRepo<M> getRepo();
}
