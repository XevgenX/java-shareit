package ru.practicum.shareit.booking.persistence.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.booking.domain.repo.BookingRepo;
import ru.practicum.shareit.booking.persistence.entity.BookingEntity;
import ru.practicum.shareit.booking.persistence.mapper.BookingPersistenceMapper;
import ru.practicum.shareit.booking.persistence.repo.BookingRepository;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingDao implements BookingRepo {
    private final BookingRepository repository;
    private final BookingPersistenceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Booking create(Booking item) {
        return mapper.toDomain(repository.save(mapper.toEntity(item)));
    }

    @Override
    @Transactional
    public Booking update(Booking item) {
        BookingEntity entity = repository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException("booking not found"));
        mapper.updateEntityFromDomain(item, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByBookerAndState(User owner, BookingStatus status) {
        return mapper.toDomainList(repository.findByBookerAndState(owner.getId(), status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByBooker(User owner) {
        return mapper.toDomainList(repository.findByBooker(owner.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByOwnerShipAndState(User owner, BookingStatus status) {
        return mapper.toDomainList(repository.findByOwnerShipAndState(owner.getId(), status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByOwnerShip(User owner) {
        return mapper.toDomainList(repository.findByOwnerShip(owner.getId()));
    }

    @Override
    public List<Booking> findByItemId(Long itemId) {
        return mapper.toDomainList(repository.findByItemId(itemId));
    }


}
