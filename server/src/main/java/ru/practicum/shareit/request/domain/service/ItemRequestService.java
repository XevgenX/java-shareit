package ru.practicum.shareit.request.domain.service;

import ru.practicum.shareit.common.domain.service.CrudService;
import ru.practicum.shareit.request.domain.model.ItemRequest;

import java.util.List;

public interface ItemRequestService extends CrudService<ItemRequest> {
    List<ItemRequest> findByRequester(Long requesterId);
}
