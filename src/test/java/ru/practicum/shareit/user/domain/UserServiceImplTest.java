package ru.practicum.shareit.user.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.domain.exception.DataConflictException;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.domain.repo.UserRepo;

import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    private UserServiceImpl userService;
    private User validUser;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepo);

        validUser = User.builder()
                .name("Иван Иванов")
                .email("valid@example.com")
                .build();

        existingUser = User.builder()
                .id(1L)
                .name("Существующий пользователь")
                .email("existing@example.com")
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(userRepo);
    }

    @Test
    @DisplayName("create успешно создает пользователя с валидными данными")
    void create_SuccessfullyCreatesUser_WithValidData() {
        when(userRepo.isEmailAlreadyExists("valid@example.com")).thenReturn(false);
        when(userRepo.create(validUser)).thenReturn(validUser);

        User created = userService.save(validUser);

        assertNotNull(created);
        verify(userRepo).create(validUser);
        verify(userRepo).isEmailAlreadyExists("valid@example.com");
    }

    @Test
    @DisplayName("create бросает ValidationException при null email")
    void create_ThrowsValidationException_WhenEmailIsNull() {
        User userWithoutEmail = User.builder()
                .name("Пользователь без email")
                .email(null)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.save(userWithoutEmail));

        assertEquals("Почта не корректна", exception.getMessage());
        verify(userRepo, never()).create(any());
        verify(userRepo, never()).isEmailAlreadyExists(any());
    }

    @Test
    @DisplayName("create бросает ValidationException при пустом email")
    void create_ThrowsValidationException_WhenEmailIsBlank() {
        User userWithBlankEmail = User.builder()
                .name("Пользователь с пустым email")
                .email("   ")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.save(userWithBlankEmail));

        assertEquals("Почта не корректна", exception.getMessage());
        verify(userRepo, never()).create(any());
        verify(userRepo, never()).isEmailAlreadyExists(any());
    }

    @Test
    @DisplayName("create бросает ValidationException при email без @")
    void create_ThrowsValidationException_WhenEmailWithoutAt() {
        User userWithInvalidEmail = User.builder()
                .name("Пользователь с невалидным email")
                .email("invalid-email.com")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.save(userWithInvalidEmail));

        assertEquals("Почта не корректна", exception.getMessage());
        verify(userRepo, never()).create(any());
        verify(userRepo, never()).isEmailAlreadyExists(any());
    }

    @Test
    @DisplayName("create бросает DataConflictException при существующем email")
    void create_ThrowsDataConflictException_WhenEmailAlreadyExists() {
        when(userRepo.isEmailAlreadyExists("existing@example.com")).thenReturn(true);

        User userWithExistingEmail = User.builder()
                .name("Пользователь с существующим email")
                .email("existing@example.com")
                .build();

        DataConflictException exception = assertThrows(DataConflictException.class,
                () -> userService.save(userWithExistingEmail));

        assertEquals("Пользователь с такой почтой уже существует", exception.getMessage());
        verify(userRepo).isEmailAlreadyExists("existing@example.com");
        verify(userRepo, never()).create(any());
    }

    @Test
    @DisplayName("findById возвращает Optional.empty при несуществующем пользователе")
    void findById_ReturnsEmptyOptional_WhenUserNotFound() {
        Long nonExistentId = 999L;
        when(userRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(nonExistentId));
        verify(userRepo).findById(nonExistentId);
    }

    @Test
    @DisplayName("findById возвращает пользователя при существующем ID")
    void findById_ReturnsUser_WhenUserExists() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));

        User result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(existingUser, result);
        verify(userRepo).findById(userId);
    }

    @Test
    @DisplayName("deleteById успешно удаляет пользователя")
    void deleteById_SuccessfullyDeletesUser() {
        Long userId = 1L;

        userService.deleteById(userId);

        verify(userRepo).deleteById(userId);
    }

    @Test
    @DisplayName("deleteById не бросает исключение при несуществующем ID")
    void deleteById_DoesNotThrowException_WhenIdDoesNotExist() {
        Long nonExistentId = 999L;

        assertDoesNotThrow(() -> userService.deleteById(nonExistentId));

        verify(userRepo).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("Создание пользователя с минимально валидным email")
    void create_SuccessfullyCreatesUser_WithMinimallyValidEmail() {
        User userWithMinimalEmail = User.builder()
                .name("Пользователь")
                .email("a@b.c")
                .build();

        when(userRepo.isEmailAlreadyExists("a@b.c")).thenReturn(false);
        when(userRepo.create(userWithMinimalEmail)).thenReturn(userWithMinimalEmail);

        User created = userService.save(userWithMinimalEmail);

        assertNotNull(created);
        verify(userRepo).isEmailAlreadyExists("a@b.c");
        verify(userRepo).create(userWithMinimalEmail);
    }

    @Test
    @DisplayName("Обновление пользователя с валидным сложным email")
    void update_SuccessfullyUpdatesUser_WithComplexValidEmail() {
        Long userId = 1L;
        String complexEmail = "user.name+tag@subdomain.example.co.uk";
        User updateData = User.builder()
                .id(userId)
                .email(complexEmail)
                .build();

        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepo.isEmailAlreadyExists(complexEmail)).thenReturn(false);
        when(userRepo.update(updateData)).thenReturn(updateData);

        User updated = userService.save(updateData);

        assertNotNull(updated);
        assertEquals(complexEmail, updated.getEmail());
        verify(userRepo).isEmailAlreadyExists(complexEmail);
    }

    @Test
    @DisplayName("Проверка на регистрозависимость email")
    void create_HandlesEmailCaseSensitivity() {
        String email = "Test@Example.COM";
        User userWithUpperCaseEmail = User.builder()
                .name("Пользователь")
                .email(email)
                .build();

        // Предполагаем, что проверка регистрозависимая
        when(userRepo.isEmailAlreadyExists(email)).thenReturn(false);
        when(userRepo.create(userWithUpperCaseEmail)).thenReturn(userWithUpperCaseEmail);

        User created = userService.save(userWithUpperCaseEmail);

        assertNotNull(created);
        verify(userRepo).isEmailAlreadyExists(email);
    }

    @Test
    @DisplayName("Полный жизненный цикл пользователя через сервис")
    void fullUserLifecycle_ThroughService() {
        // 1. Создание пользователя
        when(userRepo.isEmailAlreadyExists("newuser@example.com")).thenReturn(false);
        User userToCreate = User.builder()
                .name("Новый пользователь")
                .email("newuser@example.com")
                .build();
        User createdUser = User.builder()
                .id(1L)
                .name("Новый пользователь")
                .email("newuser@example.com")
                .build();
        when(userRepo.create(userToCreate)).thenReturn(createdUser);

        User created = userService.save(userToCreate);
        assertEquals(1L, created.getId());
        assertEquals("newuser@example.com", created.getEmail());

        // 2. Поиск пользователя
        when(userRepo.findById(1L)).thenReturn(Optional.of(createdUser));
        User found = userService.findById(1L);
        assertNotNull(found);
        assertEquals("Новый пользователь", found.getName());

        // 3. Обновление пользователя (только имя)
        User updateData = User.builder()
                .id(1L)
                .name("Обновленное имя")
                .build();
        User updatedUser = User.builder()
                .id(1L)
                .name("Обновленное имя")
                .email("newuser@example.com")
                .build();
        when(userRepo.update(updateData)).thenReturn(updatedUser);

        User updated = userService.save(updateData);
        assertEquals("Обновленное имя", updated.getName());
        assertEquals("newuser@example.com", updated.getEmail());

        // 4. Обновление email (на другой валидный)
        String newEmail = "newemail@example.com";
        User emailUpdateData = User.builder()
                .id(1L)
                .email(newEmail)
                .build();
        when(userRepo.isEmailAlreadyExists(newEmail)).thenReturn(false);
        User emailUpdatedUser = User.builder()
                .id(1L)
                .name("Обновленное имя")
                .email(newEmail)
                .build();
        when(userRepo.update(emailUpdateData)).thenReturn(emailUpdatedUser);

        User emailUpdated = userService.save(emailUpdateData);
        assertEquals(newEmail, emailUpdated.getEmail());

        // 5. Удаление пользователя
        userService.deleteById(1L);
        verify(userRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Создание нескольких пользователей с разными email")
    void create_MultipleUsers_WithDifferentEmails() {
        // Первый пользователь
        User user1 = User.builder()
                .name("Пользователь 1")
                .email("user1@example.com")
                .build();
        User createdUser1 = User.builder()
                .id(1L)
                .name("Пользователь 1")
                .email("user1@example.com")
                .build();

        when(userRepo.isEmailAlreadyExists("user1@example.com")).thenReturn(false);
        when(userRepo.create(user1)).thenReturn(createdUser1);

        User created1 = userService.save(user1);
        assertEquals(1L, created1.getId());

        // Второй пользователь
        User user2 = User.builder()
                .name("Пользователь 2")
                .email("user2@example.com")
                .build();
        User createdUser2 = User.builder()
                .id(2L)
                .name("Пользователь 2")
                .email("user2@example.com")
                .build();

        when(userRepo.isEmailAlreadyExists("user2@example.com")).thenReturn(false);
        when(userRepo.create(user2)).thenReturn(createdUser2);

        User created2 = userService.save(user2);
        assertEquals(2L, created2.getId());

        // Третий пользователь с существующим email должен выбросить исключение
        User user3 = User.builder()
                .name("Пользователь 3")
                .email("user1@example.com") // Такой же как у первого
                .build();

        when(userRepo.isEmailAlreadyExists("user1@example.com")).thenReturn(true);

        DataConflictException exception = assertThrows(DataConflictException.class,
                () -> userService.save(user3));

        assertEquals("Пользователь с такой почтой уже существует", exception.getMessage());

        // Проверяем что create вызывался только два раза
        verify(userRepo, times(2)).create(any());
    }
}
