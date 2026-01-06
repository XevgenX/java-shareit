package ru.practicum.shareit.request.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.persistence.entity.RequestEntity;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.entity.UserEntity;
import ru.practicum.shareit.user.persistence.mapper.UserPersistenceMapper;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RequestPersistenceMapper {
    private final UserPersistenceMapper userMapper;

    public ItemRequest toDomain(RequestEntity entity) {
        if (entity == null) {
            return null;
        }

        User requester = null;
        if (entity.getRequester() != null) {
            requester = userMapper.toDomain(entity.getRequester());
        }

        return ItemRequest.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .requester(requester)
                .created(entity.getCreated())
                .build();
    }

    public RequestEntity toEntity(ItemRequest domain) {
        if (domain == null) {
            return null;
        }

        UserEntity requesterEntity = null;
        if (domain.getRequester() != null) {
            requesterEntity = UserEntity.builder()
                    .id(domain.getRequester().getId())
                    .build();
        }

        return RequestEntity.builder()
                .id(domain.getId())
                .description(domain.getDescription())
                .requester(requesterEntity)
                .created(domain.getCreated())
                .build();
    }

    public RequestEntity toNewEntity(ItemRequest domain, UserEntity requesterEntity) {
        if (domain == null || requesterEntity == null) {
            return null;
        }

        return RequestEntity.builder()
                .description(domain.getDescription())
                .requester(requesterEntity)
                .created(domain.getCreated() != null ? domain.getCreated() : LocalDateTime.now())
                .build();
    }
}
