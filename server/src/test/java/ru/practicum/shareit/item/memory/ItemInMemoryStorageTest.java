package ru.practicum.shareit.item.memory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ItemInMemoryStorageTest {
    private ItemInMemoryStorage storage;
    private User testUser1;
    private User testUser2;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        storage = new ItemInMemoryStorage();

        testUser1 = User.builder()
                .id(1L)
                .name("User 1")
                .email("user1@test.com")
                .build();

        testUser2 = User.builder()
                .id(2L)
                .name("User 2")
                .email("user2@test.com")
                .build();

        item1 = Item.builder()
                .name("Дрель")
                .description("Аккумуляторная дрель мощная")
                .available(true)
                .owner(testUser1)
                .build();

        item2 = Item.builder()
                .name("Перфоратор")
                .description("Перфоратор для бетона")
                .available(false)
                .owner(testUser1)
                .build();

        item3 = Item.builder()
                .name("Шуруповерт")
                .description("Электрический шуруповерт с аккумулятором")
                .available(true)
                .owner(testUser2)
                .build();
    }

    @AfterEach
    void tearDown() {
        storage.deleteAll();
    }

    @Test
    @DisplayName("Создание нового Item с автоматической генерацией ID")
    void create_shouldGenerateId_whenIdNotProvided() {
        Item created = storage.create(item1);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
        assertEquals("Дрель", created.getName());
        assertEquals(testUser1, created.getOwner());
    }

    @Test
    @DisplayName("Исключения при попытке создать item = null")
    void create_shouldThrowException_whenItemIsNull() {
        assertThrows(IllegalArgumentException.class, () -> storage.create(null));
    }

    @Test
    @DisplayName("Последовательное присвоение ID при создании нескольких Item")
    void create_shouldIncrementIdForEachNewItem() {
        Item created1 = storage.create(item1);
        Item created2 = storage.create(item2);
        Item created3 = storage.create(item3);
        assertEquals(1L, created1.getId());
        assertEquals(2L, created2.getId());
        assertEquals(3L, created3.getId());
    }

    @Test
    @DisplayName("Успешный поиск существующего Item по ID")
    void findById_shouldReturnItem_whenExists() {
        Item created = storage.create(item1);
        Long itemId = created.getId();
        Optional<Item> found = storage.findById(itemId);
        assertTrue(found.isPresent());
        assertEquals(itemId, found.get().getId());
        assertEquals("Дрель", found.get().getName());
    }

    @Test
    @DisplayName("Возврат пустого Optional при поиске несуществующего ID")
    void findById_shouldReturnEmptyOptional_whenNotExists() {
        Optional<Item> found = storage.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Возврат пустого Optional при передаче null")
    void findById_shouldReturnEmptyOptional_whenIdIsNull() {
        Optional<Item> found = storage.findById(null);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Поиск всех Item конкретного владельца")
    void findByOwner_shouldReturnAllItemsForOwner() {
        storage.create(item1);
        storage.create(item2);
        storage.create(item3);
        List<Item> user1Items = storage.findByOwner(testUser1);
        List<Item> user2Items = storage.findByOwner(testUser2);
        assertEquals(2, user1Items.size());
        assertThat(user1Items)
                .extracting(Item::getName)
                .containsExactlyInAnyOrder("Дрель", "Перфоратор");
        assertEquals(1, user2Items.size());
        assertEquals("Шуруповерт", user2Items.get(0).getName());
    }

    @Test
    @DisplayName("Возврат пустого списка при отсутствии Item у владельца")
    void findByOwner_shouldReturnEmptyList_whenOwnerHasNoItems() {
        User newUser = User.builder()
                .id(99L)
                .name("Новый пользователь")
                .build();
        List<Item> items = storage.findByOwner(newUser);
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Возврат пустого списка при передаче null владельца")
    void findByOwner_shouldReturnEmptyList_whenOwnerIsNull() {
        List<Item> items = storage.findByOwner(null);
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Возврат пустого списка при передаче владельца с null ID")
    void findByOwner_shouldReturnEmptyList_whenOwnerIdIsNull() {
        User userWithoutId = User.builder()
                .name("User without ID")
                .build();
        List<Item> items = storage.findByOwner(userWithoutId);
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Поиск по тексту в названии (без учета регистра)")
    void findByText_shouldFindByTextInName() {
        storage.create(item1);
        storage.create(item2);
        storage.create(item3);
        List<Item> results = storage.findByTextContainsInNameAndDescription("дрель");
        assertEquals(1, results.size());
        assertEquals("Дрель", results.get(0).getName());
    }

    @Test
    @DisplayName("Поиск по тексту в описании (без учета регистра)")
    void findByText_shouldNotFindByTextInDescriptionWhenItemUnAvailable() {
        storage.create(item1);
        storage.create(item2);
        storage.create(item3);
        List<Item> results = storage.findByTextContainsInNameAndDescription("бетона");
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Поиск по тексту в описании (без учета регистра)")
    void findByText_shouldFindByTextInDescription() {
        storage.create(item1);
        storage.create(item2);
        storage.create(item3);
        List<Item> results = storage.findByTextContainsInNameAndDescription("дрель мощная");
        assertEquals(1, results.size());
        assertEquals("Дрель", results.get(0).getName());
    }

    @Test
    @DisplayName("Поиск только среди доступных Item")
    void findByText_shouldOnlySearchAvailableItems() {
        storage.create(item1);
        storage.create(item2);
        storage.create(item3);
        List<Item> results = storage.findByTextContainsInNameAndDescription("Перфоратор");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Поиск возвращает пустой список при отсутствии совпадений")
    void findByText_shouldReturnEmptyList_whenNoMatches() {
        storage.create(item1);
        storage.create(item2);
        storage.create(item3);
        List<Item> results = storage.findByTextContainsInNameAndDescription("отвертка");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Успешное обновление существующего Item")
    void update_shouldUpdateExistingItem() {
        Item createdItem = storage.create(item1);
        Item updateData = Item.builder()
                .id(createdItem.getId())
                .name("Обновленная дрель")
                .description("Новое описание")
                .available(false)
                .owner(testUser2)
                .build();
        Item updated = storage.update(updateData);
        assertEquals(createdItem.getId(), updated.getId());
        assertEquals("Обновленная дрель", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertFalse(updated.getAvailable());
        assertEquals(testUser2, updated.getOwner());
    }

    @Test
    @DisplayName("Частичное обновление (только указанные поля)")
    void update_shouldPartiallyUpdateItem() {
        Item createdItem = storage.create(item1);
        Item updateData = Item.builder()
                .id(createdItem.getId())
                .name("Новое название")
                .build();
        Item updated = storage.update(updateData);
        assertEquals("Новое название", updated.getName());
        assertEquals("Аккумуляторная дрель мощная", updated.getDescription());
        assertTrue(updated.getAvailable());
        assertEquals(testUser1, updated.getOwner());
    }

    @Test
    @DisplayName("Бросок исключения при попытке обновить несуществующий Item")
    void update_shouldThrowException_whenItemNotExists() {
        Item createdItem = storage.create(item1);
        Item nonExistentItem = Item.builder()
                .id(999L)
                .name("Несуществующий")
                .build();
        assertThrows(NoSuchElementException.class, () -> storage.update(nonExistentItem));
    }

    @Test
    @DisplayName("Бросок исключения при передаче null Item")
    void update_shouldThrowException_whenItemIsNull() {
        Item createdItem = storage.create(item1);
        assertThrows(IllegalArgumentException.class, () -> storage.update(null));
    }

    @Test
    @DisplayName("Бросок исключения при передаче Item с null ID")
    void update_shouldThrowException_whenItemIdIsNull() {
        Item createdItem = storage.create(item1);
        Item itemWithoutId = Item.builder()
                .name("Item without ID")
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> storage.update(itemWithoutId));
    }

    @Test
    @DisplayName("Нельзя изменить ID существующего Item")
    void update_shouldNotChangeItemId() {
        Item createdItem = storage.create(item1);
        Long originalId = createdItem.getId();
        Item updateData = Item.builder()
                .id(originalId)
                .name("Название")
                .build();
        Item updated = storage.update(updateData);
        assertEquals(originalId, updated.getId());
    }
}
