package ru.practicum.shareit.item.domain;

import ru.practicum.shareit.common.domain.service.CrudService;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;

public interface ItemService extends CrudService<Item> {
    List<Item> findByOwner(User user);

    List<Item> findByTextContainsInNameAndDescription(String text);
}
