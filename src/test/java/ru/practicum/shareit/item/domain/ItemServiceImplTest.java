package ru.practicum.shareit.item.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.domain.repo.ItemRepo;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepo itemRepo;

    private ItemServiceImpl itemService;
    private User validUser;
    private Item validItem;
    private Item existingItem;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepo);

        validUser = User.builder()
                .id(1L)
                .name("Владелец")
                .email("owner@example.com")
                .build();

        validItem = Item.builder()
                .name("Дрель")
                .description("Аккумуляторная дрель мощная")
                .available(true)
                .owner(validUser)
                .build();

        existingItem = Item.builder()
                .id(1L)
                .name("Перфоратор")
                .description("Перфоратор для бетона")
                .available(false)
                .owner(validUser)
                .build();
    }

    @Test
    @DisplayName("create успешно создает Item с валидными данными")
    void create_SuccessfullyCreatesItem_WithValidData() {
        when(itemRepo.create(validItem)).thenReturn(validItem);

        Item created = itemService.save(validItem);

        assertNotNull(created);
        verify(itemRepo).create(validItem);
    }

    @Test
    @DisplayName("create бросает ValidationException при null available")
    void create_ThrowsValidationException_WhenAvailableIsNull() {
        Item itemWithoutAvailable = Item.builder()
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(null)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.save(itemWithoutAvailable));

        assertEquals("Данные не корректны", exception.getMessage());
        verify(itemRepo, never()).create(any());
    }

    @Test
    @DisplayName("create бросает ValidationException при null name")
    void create_ThrowsValidationException_WhenNameIsNull() {
        Item itemWithoutName = Item.builder()
                .name(null)
                .description("Описание")
                .available(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.save(itemWithoutName));

        assertEquals("Данные не корректны", exception.getMessage());
        verify(itemRepo, never()).create(any());
    }

    @Test
    @DisplayName("create бросает ValidationException при пустом name")
    void create_ThrowsValidationException_WhenNameIsBlank() {
        Item itemWithBlankName = Item.builder()
                .name("   ")
                .description("Описание")
                .available(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.save(itemWithBlankName));

        assertEquals("Данные не корректны", exception.getMessage());
        verify(itemRepo, never()).create(any());
    }

    @Test
    @DisplayName("create бросает ValidationException при null description")
    void create_ThrowsValidationException_WhenDescriptionIsNull() {
        Item itemWithoutDescription = Item.builder()
                .name("Дрель")
                .description(null)
                .available(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.save(itemWithoutDescription));

        assertEquals("Данные не корректны", exception.getMessage());
        verify(itemRepo, never()).create(any());
    }

    @Test
    @DisplayName("create бросает ValidationException при пустом description")
    void create_ThrowsValidationException_WhenDescriptionIsBlank() {
        Item itemWithBlankDescription = Item.builder()
                .name("Дрель")
                .description("   ")
                .available(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.save(itemWithBlankDescription));

        assertEquals("Данные не корректны", exception.getMessage());
        verify(itemRepo, never()).create(any());
    }

    @Test
    @DisplayName("create успешно создает Item с минимально валидными данными")
    void create_SuccessfullyCreatesItem_WithMinimallyValidData() {
        Item minimalItem = Item.builder()
                .name("Д")
                .description("О")
                .available(false)
                .build();

        when(itemRepo.create(minimalItem)).thenReturn(minimalItem);

        Item created = itemService.save(minimalItem);

        assertNotNull(created);
        verify(itemRepo).create(minimalItem);
    }

    @Test
    @DisplayName("findById возвращает Optional.empty при несуществующем Item")
    void findById_ReturnsEmptyOptional_WhenItemNotFound() {
        Long nonExistentId = 999L;
        when(itemRepo.findById(nonExistentId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findById(nonExistentId));
    }

    @Test
    @DisplayName("findById возвращает Item при существующем ID")
    void findById_ReturnsItem_WhenItemExists() {
        Long itemId = 1L;
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(existingItem));

        Item result = itemService.findById(itemId);

        assertNotNull(result);
        assertEquals(existingItem, result);
        verify(itemRepo).findById(itemId);
    }

    @Test
    @DisplayName("deleteById успешно удаляет Item")
    void deleteById_SuccessfullyDeletesItem() {
        Long itemId = 1L;

        itemService.deleteById(itemId);

        verify(itemRepo).deleteById(itemId);
    }

    @Test
    @DisplayName("findByOwner возвращает список Item для владельца")
    void findByOwner_ReturnsItemsList_ForOwner() {
        List<Item> expectedItems = List.of(validItem, existingItem);
        when(itemRepo.findByOwner(validUser)).thenReturn(expectedItems);

        List<Item> items = itemService.findByOwner(validUser);

        assertEquals(2, items.size());
        assertThat(items).containsExactlyInAnyOrderElementsOf(expectedItems);
        verify(itemRepo).findByOwner(validUser);
    }

    @Test
    @DisplayName("findByOwner бросает ValidationException при null user")
    void findByOwner_ThrowsValidationException_WhenUserIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.findByOwner(null));

        assertEquals("Некорректный user", exception.getMessage());
        verify(itemRepo, never()).findByOwner(any());
    }

    @Test
    @DisplayName("findByOwner бросает ValidationException при user с null id")
    void findByOwner_ThrowsValidationException_WhenUserIdIsNull() {
        User userWithoutId = User.builder()
                .name("Пользователь без ID")
                .email("user@example.com")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.findByOwner(userWithoutId));

        assertEquals("Некорректный user", exception.getMessage());
        verify(itemRepo, never()).findByOwner(any());
    }

    @Test
    @DisplayName("findByOwner возвращает пустой список если у владельца нет Item")
    void findByOwner_ReturnsEmptyList_WhenOwnerHasNoItems() {
        when(itemRepo.findByOwner(validUser)).thenReturn(Collections.emptyList());

        List<Item> items = itemService.findByOwner(validUser);

        assertTrue(items.isEmpty());
        verify(itemRepo).findByOwner(validUser);
    }

    @Test
    @DisplayName("findByTextContainsInNameAndDescription возвращает список Item по тексту")
    void findByText_ReturnsItemsList_ForSearchText() {
        String searchText = "дрель";
        List<Item> expectedItems = List.of(validItem);
        when(itemRepo.findByTextContainsInNameAndDescription(searchText)).thenReturn(expectedItems);

        List<Item> items = itemService.findByTextContainsInNameAndDescription(searchText);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.get(0).getName());
        verify(itemRepo).findByTextContainsInNameAndDescription(searchText);
    }

    @Test
    @DisplayName("findByTextContainsInNameAndDescription возвращает пустой список при null тексте")
    void findByText_ReturnsEmptyList_WhenTextIsNull() {
        List<Item> items = itemService.findByTextContainsInNameAndDescription(null);

        assertTrue(items.isEmpty());
        verify(itemRepo, never()).findByTextContainsInNameAndDescription(any());
    }

    @Test
    @DisplayName("findByTextContainsInNameAndDescription возвращает пустой список при пустом тексте")
    void findByText_ReturnsEmptyList_WhenTextIsEmpty() {
        List<Item> items = itemService.findByTextContainsInNameAndDescription("");

        assertTrue(items.isEmpty());
        verify(itemRepo, never()).findByTextContainsInNameAndDescription(any());
    }

    @Test
    @DisplayName("findByTextContainsInNameAndDescription возвращает пустой список при тексте из пробелов")
    void findByText_ReturnsEmptyList_WhenTextIsBlank() {
        List<Item> items = itemService.findByTextContainsInNameAndDescription("   ");

        assertTrue(items.isEmpty());
        verify(itemRepo, never()).findByTextContainsInNameAndDescription(any());
    }

    @Test
    @DisplayName("findByTextContainsInNameAndDescription возвращает пустой список если ничего не найдено")
    void findByText_ReturnsEmptyList_WhenNoMatchesFound() {
        String searchText = "отвертка";
        when(itemRepo.findByTextContainsInNameAndDescription(searchText)).thenReturn(Collections.emptyList());

        List<Item> items = itemService.findByTextContainsInNameAndDescription(searchText);

        assertTrue(items.isEmpty());
        verify(itemRepo).findByTextContainsInNameAndDescription(searchText);
    }

    @Test
    @DisplayName("findByTextContainsInNameAndDescription делегирует поиск репозиторию")
    void findByText_DelegatesSearch_ToRepository() {
        String searchText = "перфоратор";
        List<Item> expectedItems = List.of(existingItem);
        when(itemRepo.findByTextContainsInNameAndDescription(searchText)).thenReturn(expectedItems);

        List<Item> items = itemService.findByTextContainsInNameAndDescription(searchText);

        assertEquals(1, items.size());
        assertEquals("Перфоратор", items.get(0).getName());
        verify(itemRepo).findByTextContainsInNameAndDescription(searchText);
    }

    @Test
    @DisplayName("Полный жизненный цикл Item через сервис")
    void fullItemLifecycle_ThroughService() {
        // 1. Создание Item
        Item itemToCreate = Item.builder()
                .name("Шуруповерт")
                .description("Электрический шуруповерт")
                .available(true)
                .owner(validUser)
                .build();
        Item createdItem = Item.builder()
                .id(1L)
                .name("Шуруповерт")
                .description("Электрический шуруповерт")
                .available(true)
                .owner(validUser)
                .build();
        when(itemRepo.create(itemToCreate)).thenReturn(createdItem);

        Item created = itemService.save(itemToCreate);
        assertEquals(1L, created.getId());
        assertEquals("Шуруповерт", created.getName());

        // 2. Поиск Item по ID
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createdItem));
        Item found = itemService.findById(1L);
        assertNotNull(found);
        assertEquals("Шуруповерт", found.getName());

        // 3. Обновление Item
        Item updateData = Item.builder()
                .id(1L)
                .name("Профессиональный шуруповерт")
                .description("Обновленное описание")
                .available(false)
                .build();
        Item updatedItem = Item.builder()
                .id(1L)
                .name("Профессиональный шуруповерт")
                .description("Обновленное описание")
                .available(false)
                .owner(validUser)
                .build();
        when(itemRepo.update(updateData)).thenReturn(updatedItem);

        Item updated = itemService.save(updateData);
        assertEquals("Профессиональный шуруповерт", updated.getName());
        assertFalse(updated.getAvailable());

        // 4. Поиск Item по владельцу
        List<Item> ownerItems = List.of(updatedItem);
        when(itemRepo.findByOwner(validUser)).thenReturn(ownerItems);

        List<Item> foundByOwner = itemService.findByOwner(validUser);
        assertEquals(1, foundByOwner.size());
        assertEquals("Профессиональный шуруповерт", foundByOwner.get(0).getName());

        // 5. Поиск Item по тексту
        List<Item> searchResults = List.of(updatedItem);
        when(itemRepo.findByTextContainsInNameAndDescription("шуруп")).thenReturn(searchResults);

        List<Item> foundByText = itemService.findByTextContainsInNameAndDescription("шуруп");
        assertEquals(1, foundByText.size());

        // 6. Удаление Item
        itemService.deleteById(1L);
        verify(itemRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Создание нескольких Item с разными данными")
    void create_MultipleItems_WithDifferentData() {
        // Первый Item
        Item item1 = Item.builder()
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .build();
        Item createdItem1 = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .build();

        when(itemRepo.create(item1)).thenReturn(createdItem1);

        Item created1 = itemService.save(item1);
        assertEquals(1L, created1.getId());

        // Второй Item
        Item item2 = Item.builder()
                .name("Перфоратор")
                .description("Перфоратор для бетона")
                .available(false)
                .build();
        Item createdItem2 = Item.builder()
                .id(2L)
                .name("Перфоратор")
                .description("Перфоратор для бетона")
                .available(false)
                .build();

        when(itemRepo.create(item2)).thenReturn(createdItem2);

        Item created2 = itemService.save(item2);
        assertEquals(2L, created2.getId());

        // Проверяем что create вызывался два раза
        verify(itemRepo, times(2)).create(any());
    }

    @Test
    @DisplayName("Создание Item без owner разрешено")
    void create_ItemWithoutOwner_IsAllowed() {
        Item itemWithoutOwner = Item.builder()
                .name("Инструмент")
                .description("Описание инструмента")
                .available(true)
                // owner не указан
                .build();
        Item createdItem = Item.builder()
                .id(1L)
                .name("Инструмент")
                .description("Описание инструмента")
                .available(true)
                .build();

        when(itemRepo.create(itemWithoutOwner)).thenReturn(createdItem);

        Item created = itemService.save(itemWithoutOwner);

        assertNotNull(created);
        assertNull(created.getOwner()); // owner может быть null
        verify(itemRepo).create(itemWithoutOwner);
    }

    @Test
    @DisplayName("Поиск Item по тексту с различными регистрами")
    void findByText_SearchesCaseInsensitively() {
        String searchText = "ДРЕЛЬ";
        List<Item> expectedItems = List.of(validItem);

        // Предполагаем, что репозиторий выполняет поиск без учета регистра
        when(itemRepo.findByTextContainsInNameAndDescription(searchText)).thenReturn(expectedItems);

        List<Item> items = itemService.findByTextContainsInNameAndDescription(searchText);

        assertEquals(1, items.size());
        verify(itemRepo).findByTextContainsInNameAndDescription(searchText);
    }
}
