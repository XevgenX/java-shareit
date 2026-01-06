package ru.practicum.shareit.request.domain.repo;

import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.request.domain.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepo extends CrudRepo<ItemRequest> {
    List<ItemRequest> findByRequester(Long requesterId);
}
