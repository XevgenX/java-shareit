package ru.practicum.shareit.booking.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.persistence.entity.BookingEntity;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;
import ru.practicum.shareit.item.persistence.mapper.ItemPersistenceMapper;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.entity.UserEntity;
import ru.practicum.shareit.user.persistence.mapper.UserPersistenceMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingPersistenceMapper {
    private final UserPersistenceMapper userMapper;
    private final ItemPersistenceMapper itemMapper;

    public Booking toDomain(BookingEntity entity) {
        if (entity == null) {
            return null;
        }

        Item item = null;
        if (entity.getItem() != null) {
            item = itemMapper.toDomain(entity.getItem());
        }

        User booker = null;
        if (entity.getBooker() != null) {
            booker = userMapper.toDomain(entity.getBooker());
        }

        return Booking.builder()
                .id(entity.getId())
                .start(entity.getStart())
                .end(entity.getEnd())
                .item(item)
                .booker(booker)
                .status(entity.getStatus())
                .created(entity.getCreated())
                .build();
    }

    public BookingEntity toEntity(Booking domain) {
        if (domain == null) {
            return null;
        }

        ItemEntity itemEntity = null;
        if (domain.getItem() != null) {
            itemEntity = ItemEntity.builder()
                    .id(domain.getItem().getId())
                    .build();
        }

        UserEntity bookerEntity = null;
        if (domain.getBooker() != null) {
            bookerEntity = UserEntity.builder()
                    .id(domain.getBooker().getId())
                    .build();
        }

        return BookingEntity.builder()
                .id(domain.getId())
                .start(domain.getStart())
                .end(domain.getEnd())
                .item(itemEntity)
                .booker(bookerEntity)
                .status(domain.getStatus())
                .created(domain.getCreated())
                .build();
    }

    public void updateEntityFromDomain(Booking domain, BookingEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        if (domain.getStatus() != null) {
            entity.setStatus(domain.getStatus());
        }
    }

    public List<Booking> toDomainList(List<BookingEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
}
