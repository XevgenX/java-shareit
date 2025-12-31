package ru.practicum.shareit.user.api.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("toModel преобразует UserDto в User со всеми полями")
    void toModel_ConvertsUserDtoToUser_WithAllFields() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        User user = userMapper.toModel(dto);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Иван Иванов", user.getName());
        assertEquals("ivan@example.com", user.getEmail());
    }

    @Test
    @DisplayName("toModel возвращает null при null UserDto")
    void toModel_ReturnsNull_WhenUserDtoIsNull() {
        User user = userMapper.toModel(null);

        assertNull(user);
    }

    @Test
    @DisplayName("toModel корректно обрабатывает UserDto с null полями")
    void toModel_HandlesUserDto_WithNullFields() {
        UserDto dto = UserDto.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        User user = userMapper.toModel(dto);

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("toModel преобразует UserDto через конструктор record")
    void toModel_ConvertsUserDto_FromRecordConstructor() {
        // record можно создавать и через конструктор
        UserDto dto = new UserDto(1L, "Петр Петров", "petr@example.com");

        User user = userMapper.toModel(dto);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Петр Петров", user.getName());
        assertEquals("petr@example.com", user.getEmail());
    }

    @Test
    @DisplayName("toDto преобразует User в UserDto со всеми полями")
    void toDto_ConvertsUserToUserDto_WithAllFields() {
        User user = User.builder()
                .id(1L)
                .name("Анна Сидорова")
                .email("anna@example.com")
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Анна Сидорова", dto.name());
        assertEquals("anna@example.com", dto.email());
    }

    @Test
    @DisplayName("toDto возвращает null при null User")
    void toDto_ReturnsNull_WhenUserIsNull() {
        UserDto dto = userMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("toDto корректно обрабатывает User с null полями")
    void toDto_HandlesUser_WithNullFields() {
        User user = User.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertNull(dto.id());
        assertNull(dto.name());
        assertNull(dto.email());
    }

    @Test
    @DisplayName("toModels преобразует список UserDto в список User")
    void toModels_ConvertsListOfUserDtos_ToListOfUsers() {
        UserDto dto1 = UserDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        UserDto dto2 = UserDto.builder()
                .id(2L)
                .name("Петр Петров")
                .email("petr@example.com")
                .build();

        List<UserDto> dtos = Arrays.asList(dto1, dto2);

        List<User> users = userMapper.toModels(dtos);

        assertNotNull(users);
        assertEquals(2, users.size());

        User user1 = users.get(0);
        assertEquals(1L, user1.getId());
        assertEquals("Иван Иванов", user1.getName());
        assertEquals("ivan@example.com", user1.getEmail());

        User user2 = users.get(1);
        assertEquals(2L, user2.getId());
        assertEquals("Петр Петров", user2.getName());
        assertEquals("petr@example.com", user2.getEmail());
    }

    @Test
    @DisplayName("toModels возвращает пустой список при пустом списке")
    void toModels_ReturnsEmptyList_WhenListIsEmpty() {
        List<UserDto> emptyList = Collections.emptyList();

        List<User> users = userMapper.toModels(emptyList);

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("toModels корректно обрабатывает список с null элементами")
    void toModels_HandlesList_WithNullElements() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Иван")
                .build();

        List<UserDto> dtos = Arrays.asList(dto, null);

        List<User> users = userMapper.toModels(dtos);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertNotNull(users.get(0));
        assertNull(users.get(1));
    }

    @Test
    @DisplayName("toDtos преобразует список User в список UserDto")
    void toDtos_ConvertsListOfUsers_ToListOfUserDtos() {
        User user1 = User.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Петр Петров")
                .email("petr@example.com")
                .build();

        List<User> users = Arrays.asList(user1, user2);

        List<UserDto> dtos = userMapper.toDtos(users);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        UserDto dto1 = dtos.get(0);
        assertEquals(1L, dto1.id());
        assertEquals("Иван Иванов", dto1.name());
        assertEquals("ivan@example.com", dto1.email());

        UserDto dto2 = dtos.get(1);
        assertEquals(2L, dto2.id());
        assertEquals("Петр Петров", dto2.name());
        assertEquals("petr@example.com", dto2.email());
    }

    @Test
    @DisplayName("toDtos возвращает пустой список при пустом списке")
    void toDtos_ReturnsEmptyList_WhenListIsEmpty() {
        List<User> emptyList = Collections.emptyList();

        List<UserDto> dtos = userMapper.toDtos(emptyList);

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("toDtos корректно обрабатывает список с null элементами")
    void toDtos_HandlesList_WithNullElements() {
        User user = User.builder()
                .id(1L)
                .name("Иван")
                .build();

        List<User> users = Arrays.asList(user, null);

        List<UserDto> dtos = userMapper.toDtos(users);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertNotNull(dtos.get(0));
        assertNull(dtos.get(1));
    }

    @Test
    @DisplayName("Двустороннее преобразование User -> DTO -> User сохраняет данные")
    void bidirectionalConversionUserToDtoToUser_PreservesData() {
        User originalUser = User.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        UserDto dto = userMapper.toDto(originalUser);
        User convertedUser = userMapper.toModel(dto);

        assertNotNull(convertedUser);
        assertEquals(originalUser.getId(), convertedUser.getId());
        assertEquals(originalUser.getName(), convertedUser.getName());
        assertEquals(originalUser.getEmail(), convertedUser.getEmail());
    }

    @Test
    @DisplayName("Двустороннее преобразование DTO -> User -> DTO сохраняет данные")
    void bidirectionalConversionDtoToUserToDto_PreservesData() {
        UserDto originalDto = UserDto.builder()
                .id(1L)
                .name("Петр Петров")
                .email("petr@example.com")
                .build();

        User user = userMapper.toModel(originalDto);
        UserDto convertedDto = userMapper.toDto(user);

        assertNotNull(convertedDto);
        assertEquals(originalDto.id(), convertedDto.id());
        assertEquals(originalDto.name(), convertedDto.name());
        assertEquals(originalDto.email(), convertedDto.email());
    }

    @Test
    @DisplayName("toModels сохраняет порядок элементов")
    void toModels_PreservesOrderOfElements() {
        UserDto dto1 = UserDto.builder().id(1L).name("Первый").build();
        UserDto dto2 = UserDto.builder().id(2L).name("Второй").build();
        UserDto dto3 = UserDto.builder().id(3L).name("Третий").build();

        List<UserDto> dtos = Arrays.asList(dto1, dto2, dto3);

        List<User> users = userMapper.toModels(dtos);

        assertEquals(3, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals(2L, users.get(1).getId());
        assertEquals(3L, users.get(2).getId());
    }

    @Test
    @DisplayName("toDtos сохраняет порядок элементов")
    void toDtos_PreservesOrderOfElements() {
        User user1 = User.builder().id(1L).name("Первый").build();
        User user2 = User.builder().id(2L).name("Второй").build();
        User user3 = User.builder().id(3L).name("Третий").build();

        List<User> users = Arrays.asList(user1, user2, user3);

        List<UserDto> dtos = userMapper.toDtos(users);

        assertEquals(3, dtos.size());
        assertEquals(1L, dtos.get(0).id());
        assertEquals(2L, dtos.get(1).id());
        assertEquals(3L, dtos.get(2).id());
    }

    @Test
    @DisplayName("toModels с большим списком пользователей")
    void toModels_WithLargeList() {
        List<UserDto> dtos = Arrays.asList(
                UserDto.builder().id(1L).name("User1").email("user1@example.com").build(),
                UserDto.builder().id(2L).name("User2").email("user2@example.com").build(),
                UserDto.builder().id(3L).name("User3").email("user3@example.com").build(),
                UserDto.builder().id(4L).name("User4").email("user4@example.com").build(),
                UserDto.builder().id(5L).name("User5").email("user5@example.com").build()
        );

        List<User> users = userMapper.toModels(dtos);

        assertEquals(5, users.size());
        for (int i = 0; i < 5; i++) {
            assertEquals((long) i + 1, users.get(i).getId());
            assertEquals("User" + (i + 1), users.get(i).getName());
            assertEquals("user" + (i + 1) + "@example.com", users.get(i).getEmail());
        }
    }

    @Test
    @DisplayName("toModels и toDtos работают согласованно")
    void toModelsAndToDtos_WorkConsistently() {
        UserDto dto1 = UserDto.builder().id(1L).name("User A").email("a@example.com").build();
        UserDto dto2 = UserDto.builder().id(2L).name("User B").email("b@example.com").build();

        List<UserDto> originalDtos = Arrays.asList(dto1, dto2);

        List<User> users = userMapper.toModels(originalDtos);
        List<UserDto> convertedDtos = userMapper.toDtos(users);

        assertThat(convertedDtos)
                .usingRecursiveComparison()
                .isEqualTo(originalDtos);
    }

    @Test
    @DisplayName("Преобразование User с минимальными данными")
    void toDto_WithMinimalUser() {
        User user = User.builder()
                .id(1L)
                .name("Минимальный")
                // email не установлен
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Минимальный", dto.name());
        assertNull(dto.email());
    }

    @Test
    @DisplayName("Преобразование UserDto с минимальными данными")
    void toModel_WithMinimalUserDto() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Минимальный")
                // email не установлен
                .build();

        User user = userMapper.toModel(dto);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Минимальный", user.getName());
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("Пользователь с длинным именем корректно преобразуется")
    void toDto_WithLongName() {
        String longName = "Иванов Иван Иванович Петрович Сидорович Александрович";
        User user = User.builder()
                .id(1L)
                .name(longName)
                .email("ivanov@example.com")
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(longName, dto.name());
    }

    @Test
    @DisplayName("Пользователь со сложным email корректно преобразуется")
    void toDto_WithComplexEmail() {
        String complexEmail = "user.name+tag@subdomain.example.co.uk";
        User user = User.builder()
                .id(1L)
                .name("Пользователь")
                .email(complexEmail)
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(complexEmail, dto.email());
    }

    @Test
    @DisplayName("Специальные символы в имени сохраняются")
    void toDto_PreservesSpecialCharactersInName() {
        String nameWithSpecialChars = "Иванов-Петров И.И. (генеральный директор)";
        User user = User.builder()
                .id(1L)
                .name(nameWithSpecialChars)
                .email("ivanov@example.com")
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(nameWithSpecialChars, dto.name());
    }

    @Test
    @DisplayName("Email в верхнем регистре сохраняется")
    void toDto_PreservesUpperCaseEmail() {
        String upperCaseEmail = "USER@EXAMPLE.COM";
        User user = User.builder()
                .id(1L)
                .name("Пользователь")
                .email(upperCaseEmail)
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(upperCaseEmail, dto.email());
    }

    @Test
    @DisplayName("null значения полей не ломают преобразование")
    void conversion_HandlesNullFieldValues_Gracefully() {
        User user = User.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        UserDto dto = userMapper.toDto(user);
        User convertedUser = userMapper.toModel(dto);

        assertNotNull(convertedUser);
        assertNull(convertedUser.getId());
        assertNull(convertedUser.getName());
        assertNull(convertedUser.getEmail());
    }

    @Test
    @DisplayName("Список с одним элементом корректно преобразуется")
    void toModels_WithSingleElementList() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Единственный")
                .email("single@example.com")
                .build();

        List<UserDto> dtos = Collections.singletonList(dto);

        List<User> users = userMapper.toModels(dtos);

        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals("Единственный", users.get(0).getName());
        assertEquals("single@example.com", users.get(0).getEmail());
    }

    @Test
    @DisplayName("Маппинг через конструктор record и builder дают одинаковый результат")
    void mapping_WithRecordConstructorAndBuilder_GivesSameResult() {
        // Создаем DTO через builder
        UserDto dtoFromBuilder = UserDto.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@example.com")
                .build();

        UserDto dtoFromConstructor = new UserDto(1L, "Иван", "ivan@example.com");

        User userFromBuilder = userMapper.toModel(dtoFromBuilder);
        User userFromConstructor = userMapper.toModel(dtoFromConstructor);

        assertThat(userFromBuilder)
                .usingRecursiveComparison()
                .isEqualTo(userFromConstructor);
    }

    @Test
    @DisplayName("Преобразование пользователя с максимально длинными полями")
    void toDto_WithMaximumLengthFields() {
        String maxLengthName = "А".repeat(255); // Предполагаем максимальную длину
        String maxLengthEmail = "a".repeat(100) + "@" + "b".repeat(100) + ".com";

        User user = User.builder()
                .id(Long.MAX_VALUE)
                .name(maxLengthName)
                .email(maxLengthEmail)
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(Long.MAX_VALUE, dto.id());
        assertEquals(maxLengthName, dto.name());
        assertEquals(maxLengthEmail, dto.email());
    }

    @Test
    @DisplayName("Пользователь с разными типами символов в email")
    void toDto_WithVariousCharactersInEmail() {
        String emailWithVariousChars = "user.name+tag-test_123@sub-domain.example.com";
        User user = User.builder()
                .id(1L)
                .name("Тест")
                .email(emailWithVariousChars)
                .build();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(emailWithVariousChars, dto.email());
    }
}
