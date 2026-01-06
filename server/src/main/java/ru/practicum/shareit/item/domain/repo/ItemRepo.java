package ru.practicum.shareit.item.domain.repo;

import ru.practicum.shareit.common.domain.repo.CrudRepo;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;

public interface ItemRepo extends CrudRepo<Item> {
    List<Item> findByOwner(User user);

    List<Item> findByTextContainsInNameAndDescription(String text);

    List<Item> findByRequestId(Long requestId);
}
