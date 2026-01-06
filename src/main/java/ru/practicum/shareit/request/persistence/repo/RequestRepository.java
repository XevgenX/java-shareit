package ru.practicum.shareit.request.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.persistence.entity.RequestEntity;

@Repository
public interface RequestRepository extends CrudRepository<RequestEntity, Long> {
}
