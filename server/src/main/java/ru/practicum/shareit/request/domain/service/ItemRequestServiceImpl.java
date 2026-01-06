package ru.practicum.shareit.request.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.common.domain.service.CrudServiceImpl;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.domain.repo.ItemRequestRepo;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl extends CrudServiceImpl<ItemRequest> implements ItemRequestService {
    private final ItemRequestRepo repo;

    @Override
    protected void validateBeforeCreate(ItemRequest model) {
        if (Objects.isNull(model)) {
            throw new ValidationException("Некорректный request");
        }
    }

    @Override
    protected void validateBeforePatch(ItemRequest model) {
        if (Objects.isNull(model)) {
            throw new ValidationException("Некорректный request");
        }
    }

    @Override
    protected CrudRepo<ItemRequest> getRepo() {
        return repo;
    }

    @Override
    public List<ItemRequest> findByRequester(Long requesterId) {
        return repo.findByRequester(requesterId);
    }
}
