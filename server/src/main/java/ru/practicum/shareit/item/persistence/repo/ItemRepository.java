package ru.practicum.shareit.item.persistence.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<ItemEntity, Long> {
    List<ItemEntity> findByOwnerId(Long userId);

    @Query("SELECT i FROM ItemEntity i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<ItemEntity> findByTextContainsInNameAndDescription(@Param("text") String text);

    List<ItemEntity> findByRequestId(Long requestId);
}
