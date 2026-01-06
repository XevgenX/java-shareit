package ru.practicum.shareit.common.memory;

import ru.practicum.shareit.common.domain.model.Model;
import ru.practicum.shareit.common.domain.repo.CrudRepo;
import java.util.*;

public abstract class CrudInMemoryStorage<M extends Model> implements CrudRepo<M> {
    protected final Map<Long, M> items = new HashMap<>();
    private Long idSequence = 1L;

    @Override
    public Optional<M> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(items.get(id));
    }

     @Override
    public M create(M item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        Long newId = idSequence++;
        M newItem = createCopy(item, newId);
        items.put(newId, newItem);
        return newItem;
    }

    @Override
    public M update(M item) {
        if (item == null || item.getId() == null) {
            throw new IllegalArgumentException("Item and item ID cannot be null");
        }
        if (!items.containsKey(item.getId())) {
            throw new NoSuchElementException("Item with id " + item.getId() + " not found");
        }
        M existingItem = items.get(item.getId());
        M updatedItem = createCopyWithPartialUpdate(item, existingItem);
        items.put(updatedItem.getId(), updatedItem);
        return updatedItem;
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
    }

    public void deleteAll() {
        items.clear();
    }

    protected abstract M createCopy(M model, Long newId);

    protected abstract M createCopyWithPartialUpdate(M newModel, M existingModel);
}
