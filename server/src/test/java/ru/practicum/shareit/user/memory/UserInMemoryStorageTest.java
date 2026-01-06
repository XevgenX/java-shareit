package ru.practicum.shareit.user.memory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.practicum.shareit.user.domain.model.User;

import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class UserInMemoryStorageTest {

    private UserInMemoryStorage storage;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        storage = new UserInMemoryStorage();

        user1 = User.builder()
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        user2 = User.builder()
                .name("Петр Петров")
                .email("petr@example.com")
                .build();
    }

    @AfterEach
    void tearDown() {
        storage.deleteAll();
    }

    @Test
    @DisplayName("findById возвращает Optional.empty при null ID")
    void findById_ReturnsEmptyOptional_WhenIdIsNull() {
        Optional<User> result = storage.findById(null);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findById возвращает Optional.empty при несуществующем ID")
    void findById_ReturnsEmptyOptional_WhenIdDoesNotExist() {
        Optional<User> result = storage.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findById возвращает пользователя при существующем ID")
    void findById_ReturnsUser_WhenIdExists() {
        User created = storage.create(user1);
        Long userId = created.getId();

        Optional<User> result = storage.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("Иван Иванов", result.get().getName());
        assertEquals("ivan@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("create генерирует новый ID при успешном создании")
    void create_GeneratesNewId_WhenUserCreated() {
        User created = storage.create(user1);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
    }

    @Test
    @DisplayName("create сохраняет все поля пользователя")
    void create_SavesAllUserFields() {
        User created = storage.create(user1);

        assertEquals("Иван Иванов", created.getName());
        assertEquals("ivan@example.com", created.getEmail());
        assertTrue(storage.findById(created.getId()).isPresent());
    }

    @Test
    @DisplayName("create бросает исключение при null пользователе")
    void create_ThrowsException_WhenUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> storage.create(null));
    }

    @Test
    @DisplayName("create последовательно инкрементирует ID")
    void create_IncrementsIdSequentially() {
        User created1 = storage.create(user1);
        User created2 = storage.create(user2);

        assertEquals(1L, created1.getId());
        assertEquals(2L, created2.getId());
    }

    @Test
    @DisplayName("update обновляет существующего пользователя")
    void update_UpdatesExistingUser() {
        User created = storage.create(user1);
        Long userId = created.getId();

        User updateData = User.builder()
                .id(userId)
                .name("Новое имя")
                .email("new@email.com")
                .build();

        User updated = storage.update(updateData);

        assertEquals(userId, updated.getId());
        assertEquals("Новое имя", updated.getName());
        assertEquals("new@email.com", updated.getEmail());

        Optional<User> found = storage.findById(userId);
        assertTrue(found.isPresent());
        assertEquals("Новое имя", found.get().getName());
    }

    @Test
    @DisplayName("update частично обновляет пользователя")
    void update_PartiallyUpdatesUser() {
        User created = storage.create(user1);
        Long userId = created.getId();

        User updateData = User.builder()
                .id(userId)
                .name("Новое имя")
                .build();

        User updated = storage.update(updateData);

        assertEquals("Новое имя", updated.getName());
        assertEquals("ivan@example.com", updated.getEmail()); // Осталось прежним
    }

    @Test
    @DisplayName("update бросает исключение при null пользователе")
    void update_ThrowsException_WhenUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> storage.update(null));
    }

    @Test
    @DisplayName("update бросает исключение при null ID")
    void update_ThrowsException_WhenUserIdIsNull() {
        User userWithoutId = User.builder()
                .name("Без ID")
                .build();

        assertThrows(IllegalArgumentException.class, () -> storage.update(userWithoutId));
    }

    @Test
    @DisplayName("update бросает исключение при несуществующем ID")
    void update_ThrowsException_WhenIdDoesNotExist() {
        User nonExistentUser = User.builder()
                .id(999L)
                .name("Несуществующий")
                .build();

        assertThrows(NoSuchElementException.class, () -> storage.update(nonExistentUser));
    }

    @Test
    @DisplayName("deleteById удаляет пользователя")
    void deleteById_RemovesUser() {
        User created = storage.create(user1);
        Long userId = created.getId();

        assertTrue(storage.findById(userId).isPresent());

        storage.deleteById(userId);

        assertFalse(storage.findById(userId).isPresent());
    }

    @Test
    @DisplayName("deleteById не бросает исключение при несуществующем ID")
    void deleteById_DoesNotThrowException_WhenIdDoesNotExist() {
        assertDoesNotThrow(() -> storage.deleteById(999L));
    }

    @Test
    @DisplayName("deleteAll очищает хранилище")
    void deleteAll_ClearsStorage() {
        storage.create(user1);
        storage.create(user2);

        storage.deleteAll();

        assertFalse(storage.findById(1L).isPresent());
        assertFalse(storage.findById(2L).isPresent());
    }

    @Test
    @DisplayName("isEmailAlreadyExists возвращает true при существующем email")
    void isEmailAlreadyExists_ReturnsTrue_WhenEmailExists() {
        storage.create(user1);
        boolean exists = storage.isEmailAlreadyExists("ivan@example.com");
        assertTrue(exists);
    }

    @Test
    @DisplayName("isEmailAlreadyExists возвращает false при несуществующем email")
    void isEmailAlreadyExists_ReturnsFalse_WhenEmailDoesNotExist() {
        storage.create(user1);
        boolean exists = storage.isEmailAlreadyExists("nonexistent@example.com");
        assertFalse(exists);
    }

    @Test
    @DisplayName("isEmailAlreadyExists игнорирует регистр")
    void isEmailAlreadyExists_IsCaseInsensitive() {
        storage.create(user1);
        boolean exists = storage.isEmailAlreadyExists("IVAN@EXAMPLE.COM");
        assertTrue(exists);
    }

    @Test
    @DisplayName("isEmailAlreadyExists возвращает false при пустом хранилище")
    void isEmailAlreadyExists_ReturnsFalse_WhenStorageIsEmpty() {
        boolean exists = storage.isEmailAlreadyExists("test@example.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("createCopy создает корректную копию с новым ID")
    void createCopy_CreatesCorrectCopyWithNewId() {
        Long newId = 100L;

        User copy = storage.createCopy(user1, newId);

        assertEquals(newId, copy.getId());
        assertEquals(user1.getName(), copy.getName());
        assertEquals(user1.getEmail(), copy.getEmail());
        assertNotSame(user1, copy);
    }

    @Test
    @DisplayName("createCopyWithPartialUpdate объединяет поля из двух моделей")
    void createCopyWithPartialUpdate_MergesFieldsFromBothModels() {
        User existing = User.builder()
                .id(1L)
                .name("Старое имя")
                .email("old@email.com")
                .build();

        User newModel = User.builder()
                .id(1L)
                .name("Новое имя")
                // email не установлен
                .build();

        User merged = storage.createCopyWithPartialUpdate(newModel, existing);

        assertEquals(1L, merged.getId());
        assertEquals("Новое имя", merged.getName()); // Из newModel
        assertEquals("old@email.com", merged.getEmail()); // Из existing
    }

    @Test
    @DisplayName("Полный жизненный цикл пользователя: создание, обновление, удаление")
    void fullUserLifecycle() {
        User created = storage.create(user1);
        Long userId = created.getId();

        Optional<User> found = storage.findById(userId);
        assertTrue(found.isPresent());
        assertEquals("Иван Иванов", found.get().getName());

        User updateData = User.builder()
                .id(userId)
                .name("Иван Петров")
                .email("ivan.petrov@example.com")
                .build();

        User updated = storage.update(updateData);
        assertEquals("Иван Петров", updated.getName());
        assertEquals("ivan.petrov@example.com", updated.getEmail());

        Optional<User> afterUpdate = storage.findById(userId);
        assertTrue(afterUpdate.isPresent());
        assertEquals("Иван Петров", afterUpdate.get().getName());

        assertTrue(storage.isEmailAlreadyExists("ivan.petrov@example.com"));

        storage.deleteById(userId);
        assertFalse(storage.findById(userId).isPresent());
        assertFalse(storage.isEmailAlreadyExists("ivan.petrov@example.com"));
    }

    @Test
    @DisplayName("Несколько пользователей с разными email")
    void multipleUsersWithDifferentEmails() {
        User created1 = storage.create(user1);
        User created2 = storage.create(user2);

        assertTrue(storage.isEmailAlreadyExists("ivan@example.com"));
        assertTrue(storage.isEmailAlreadyExists("petr@example.com"));
        assertFalse(storage.isEmailAlreadyExists("nonexistent@example.com"));

        Optional<User> found1 = storage.findById(created1.getId());
        Optional<User> found2 = storage.findById(created2.getId());

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertEquals("Иван Иванов", found1.get().getName());
        assertEquals("Петр Петров", found2.get().getName());
    }
}
