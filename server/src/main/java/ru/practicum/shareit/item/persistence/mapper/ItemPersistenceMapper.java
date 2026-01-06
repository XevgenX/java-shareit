package ru.practicum.shareit.item.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.persistence.entity.RequestEntity;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.entity.UserEntity;
import ru.practicum.shareit.user.persistence.mapper.UserPersistenceMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemPersistenceMapper {
    private final UserPersistenceMapper userMapper;

    public Item toDomain(ItemEntity entity) {
        if (entity == null) {
            return null;
        }

        User owner = null;
        if (entity.getOwner() != null) {
            owner = userMapper.toDomain(entity.getOwner());
        }

        ItemRequest request = null;
        if (entity.getRequest() != null) {
            request = ItemRequest.builder()
                    .id(entity.getRequest().getId())
                    .build();
        }

        return Item.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .available(entity.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    public ItemEntity toEntity(Item domain) {
        if (domain == null) {
            return null;
        }

        UserEntity ownerEntity = null;
        if (domain.getOwner() != null) {
            ownerEntity = UserEntity.builder()
                    .id(domain.getOwner().getId())
                    .build();
        }

        RequestEntity requestEntity = null;
        if (domain.getRequest() != null) {
            requestEntity = RequestEntity.builder().id(domain.getRequest().getId()).build();
        }

        return ItemEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .available(domain.getAvailable())
                .owner(ownerEntity)
                .request(requestEntity)
                .build();
    }

    public void updateEntityFromDomain(Item domain, ItemEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        if (domain.getName() != null) {
            entity.setName(domain.getName());
        }

        if (domain.getDescription() != null) {
            entity.setDescription(domain.getDescription());
        }

        if (domain.getAvailable() != null) {
            entity.setAvailable(domain.getAvailable());
        }
    }

    public List<Item> toDomainList(List<ItemEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
}