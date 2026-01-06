package ru.practicum.shareit.item.persistence.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.domain.repo.ItemRepo;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;
import ru.practicum.shareit.item.persistence.mapper.ItemPersistenceMapper;
import ru.practicum.shareit.item.persistence.repo.ItemRepository;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class ItemDao implements ItemRepo {
    private final ItemRepository repository;
    private final ItemPersistenceMapper mapper;

    @Override
    public Optional<Item> findById(Long id) {
        Optional<ItemEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        Item item = mapper.toDomain(entity.get());
        return Optional.of(item);
    }

    @Override
    public Item create(Item item) {
        return mapper.toDomain(repository.save(mapper.toEntity(item)));
    }

    @Override
    public Item update(Item item) {
        ItemEntity entity = repository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        mapper.updateEntityFromDomain(item, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Item> findByOwner(User user) {
        return mapper.toDomainList(repository.findByOwnerId(user.getId()));
    }

    @Override
    public List<Item> findByTextContainsInNameAndDescription(String text) {
        return mapper.toDomainList(repository.findByTextContainsInNameAndDescription(text));
    }

    @Override
    public List<Item> findByRequestId(Long requestId) {
        return mapper.toDomainList(repository.findByRequestId(requestId));
    }
}
