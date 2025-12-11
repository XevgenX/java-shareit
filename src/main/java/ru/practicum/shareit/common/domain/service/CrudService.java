package ru.practicum.shareit.common.domain.service;

import ru.practicum.shareit.common.domain.model.Model;

public interface CrudService<M extends Model> {

    M findById(Long id);

    M save(M item);

    void deleteById(Long id);
}
