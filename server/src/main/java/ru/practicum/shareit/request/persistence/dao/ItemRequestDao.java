package ru.practicum.shareit.request.persistence.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.domain.repo.ItemRequestRepo;
import ru.practicum.shareit.request.persistence.entity.RequestEntity;
import ru.practicum.shareit.request.persistence.mapper.RequestPersistenceMapper;
import ru.practicum.shareit.request.persistence.repo.RequestRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemRequestDao implements ItemRequestRepo {
    private final RequestRepository repository;
    private final RequestPersistenceMapper mapper;

    @Override
    public List<ItemRequest> findByRequester(Long requesterId) {
        return mapper.toDomainList(repository.findByRequesterId(requesterId));
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        Optional<RequestEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        ItemRequest item = mapper.toDomain(entity.get());
        return Optional.of(item);
    }

    @Override
    public ItemRequest create(ItemRequest item) {
        return mapper.toDomain(repository.save(mapper.toEntity(item)));
    }

    @Override
    public ItemRequest update(ItemRequest item) {
        return mapper.toDomain(repository.save(mapper.toEntity(item)));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
