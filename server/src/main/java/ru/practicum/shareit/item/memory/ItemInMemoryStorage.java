package ru.practicum.shareit.item.memory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.memory.CrudInMemoryStorage;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.domain.repo.ItemRepo;
import ru.practicum.shareit.user.domain.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryStorage extends CrudInMemoryStorage<Item> implements ItemRepo {

    @Override
    public List<Item> findByOwner(User user) {
        if (user == null || user.getId() == null) {
            return Collections.emptyList();
        }

        return items.values().stream()
                .filter(item -> item.getOwner() != null)
                .filter(item -> user.getId().equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByTextContainsInNameAndDescription(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase().trim();

        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item -> containsText(item, searchText))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByRequestId(Long requestId) {
        return List.of();
    }

    @Override
    protected Item createCopy(Item model, Long newId) {
        return Item.builder()
                .id(newId)
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .owner(model.getOwner())
                .request(model.getRequest())
                .build();
    }

    @Override
    protected Item createCopyWithPartialUpdate(Item newModel, Item existingModel) {
        return Item.builder()
                .id(existingModel.getId()) // ID не меняем
                .name(newModel.getName() != null ? newModel.getName() : existingModel.getName())
                .description(newModel.getDescription() != null ? newModel.getDescription() : existingModel.getDescription())
                .available(newModel.getAvailable() != null ? newModel.getAvailable() : existingModel.getAvailable())
                .owner(newModel.getOwner() != null ? newModel.getOwner() : existingModel.getOwner())
                .request(newModel.getRequest() != null ? newModel.getRequest() : existingModel.getRequest())
                .build();
    }

    private boolean containsText(Item item, String searchText) {
        boolean nameContains = item.getName() != null &&
                item.getName().toLowerCase().contains(searchText);
        boolean descriptionContains = item.getDescription() != null &&
                item.getDescription().toLowerCase().contains(searchText);
        return nameContains || descriptionContains;
    }
}
