package ru.practicum.shareit.common.domain.repo;

import ru.practicum.shareit.common.domain.model.Model;
import java.util.Optional;

public interface CrudRepo<M extends Model> {

    Optional<M> findById(Long id);

    M create(M item);

    M update(M item);

    void deleteById(Long id);
}
