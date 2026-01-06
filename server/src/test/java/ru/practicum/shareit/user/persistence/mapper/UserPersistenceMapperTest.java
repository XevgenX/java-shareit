package ru.practicum.shareit.user.persistence.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPersistenceMapperTest {
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String USER_EMAIL = "john.doe@example.com";
    private UserPersistenceMapper userPersistenceMapper;

    private User user;
    private UserEntity userEntity;


    @BeforeEach
    void setUp() {
        userPersistenceMapper = new UserPersistenceMapper();

        user = User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        userEntity = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();
    }

    @Test
    void toDomain_whenEntityIsNull_shouldReturnNull() {
        User result = userPersistenceMapper.toDomain(null);

        assertNull(result);
    }

    @Test
    void toDomain_whenEntityIsValid_shouldMapCorrectly() {
        User result = userPersistenceMapper.toDomain(userEntity);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toDomain_whenEntityHasNullName_shouldMapWithNullName() {
        UserEntity entityWithNullName = UserEntity.builder()
                .id(USER_ID)
                .name(null)
                .email(USER_EMAIL)
                .build();

        User result = userPersistenceMapper.toDomain(entityWithNullName);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertNull(result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toDomain_whenEntityHasEmptyName_shouldMapWithEmptyName() {
        UserEntity entityWithEmptyName = UserEntity.builder()
                .id(USER_ID)
                .name("")
                .email(USER_EMAIL)
                .build();

        User result = userPersistenceMapper.toDomain(entityWithEmptyName);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals("", result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toDomain_whenEntityHasNullEmail_shouldMapWithNullEmail() {
        UserEntity entityWithNullEmail = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(null)
                .build();

        User result = userPersistenceMapper.toDomain(entityWithNullEmail);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(USER_NAME, result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void toDomain_whenEntityHasEmptyEmail_shouldMapWithEmptyEmail() {
        UserEntity entityWithEmptyEmail = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email("")
                .build();

        User result = userPersistenceMapper.toDomain(entityWithEmptyEmail);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals("", result.getEmail());
    }

    @Test
    void toDomain_whenEntityHasNullId_shouldMapWithNullId() {
        UserEntity entityWithNullId = UserEntity.builder()
                .id(null)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        User result = userPersistenceMapper.toDomain(entityWithNullId);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toEntity_whenDomainIsNull_shouldReturnNull() {
        UserEntity result = userPersistenceMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void toEntity_whenDomainIsValid_shouldMapCorrectly() {
        UserEntity result = userPersistenceMapper.toEntity(user);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toEntity_whenDomainHasNullFields_shouldMapWithNullFields() {
        User userWithNullFields = User.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        UserEntity result = userPersistenceMapper.toEntity(userWithNullFields);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void toEntity_whenDomainHasEmptyStrings_shouldMapWithEmptyStrings() {
        User userWithEmptyFields = User.builder()
                .id(USER_ID)
                .name("")
                .email("")
                .build();

        UserEntity result = userPersistenceMapper.toEntity(userWithEmptyFields);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals("", result.getName());
        assertEquals("", result.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainIsNull_shouldNotUpdate() {
        UserEntity originalEntity = UserEntity.builder()
                .id(USER_ID)
                .name("Original Name")
                .email("original@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(null, originalEntity);

        assertEquals(USER_ID, originalEntity.getId());
        assertEquals("Original Name", originalEntity.getName());
        assertEquals("original@email.com", originalEntity.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainNameIsNotNullAndNotBlank_shouldUpdateName() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name("Old Name")
                .email("old@email.com")
                .build();

        User domainWithNewName = User.builder()
                .name("New Name")
                .email("old@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithNewName, entityToUpdate);

        assertEquals(USER_ID, entityToUpdate.getId());
        assertEquals("New Name", entityToUpdate.getName());
        assertEquals("old@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainNameIsNull_shouldNotUpdateName() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name("Original Name")
                .email("email@example.com")
                .build();

        User domainWithNullName = User.builder()
                .name(null)
                .email("new@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithNullName, entityToUpdate);

        assertEquals("Original Name", entityToUpdate.getName());
        assertEquals("new@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainNameIsBlank_shouldNotUpdateName() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name("Original Name")
                .email("email@example.com")
                .build();

        User domainWithBlankName = User.builder()
                .name("   ")
                .email("new@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithBlankName, entityToUpdate);

        assertEquals("Original Name", entityToUpdate.getName());
        assertEquals("new@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainEmailIsNotNullAndNotBlank_shouldUpdateEmail() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email("old@email.com")
                .build();

        User domainWithNewEmail = User.builder()
                .name(USER_NAME)
                .email("new@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithNewEmail, entityToUpdate);

        assertEquals(USER_ID, entityToUpdate.getId());
        assertEquals(USER_NAME, entityToUpdate.getName());
        assertEquals("new@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainEmailIsNull_shouldNotUpdateEmail() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email("original@email.com")
                .build();

        User domainWithNullEmail = User.builder()
                .name("New Name")
                .email(null)
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithNullEmail, entityToUpdate);

        assertEquals("New Name", entityToUpdate.getName());
        assertEquals("original@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainEmailIsBlank_shouldNotUpdateEmail() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email("original@email.com")
                .build();

        User domainWithBlankEmail = User.builder()
                .name("New Name")
                .email("  ")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithBlankEmail, entityToUpdate);

        assertEquals("New Name", entityToUpdate.getName());
        assertEquals("original@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenBothNameAndEmailAreValid_shouldUpdateBoth() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name("Old Name")
                .email("old@email.com")
                .build();

        User domainWithUpdates = User.builder()
                .name("New Name")
                .email("new@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithUpdates, entityToUpdate);

        assertEquals(USER_ID, entityToUpdate.getId());
        assertEquals("New Name", entityToUpdate.getName());
        assertEquals("new@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainHasId_shouldNotUpdateId() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        User domainWithId = User.builder()
                .id(999L)
                .name("New Name")
                .email("new@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithId, entityToUpdate);

        assertEquals(USER_ID, entityToUpdate.getId());
        assertEquals("New Name", entityToUpdate.getName());
        assertEquals("new@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainNameIsEmptyString_shouldNotUpdateName() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name("Original Name")
                .email(USER_EMAIL)
                .build();

        User domainWithEmptyName = User.builder()
                .name("")
                .email("new@email.com")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithEmptyName, entityToUpdate);

        assertEquals("Original Name", entityToUpdate.getName());
        assertEquals("new@email.com", entityToUpdate.getEmail());
    }

    @Test
    void updateEntityFromDomain_whenDomainEmailIsEmptyString_shouldNotUpdateEmail() {
        UserEntity entityToUpdate = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email("original@email.com")
                .build();

        User domainWithEmptyEmail = User.builder()
                .name("New Name")
                .email("")
                .build();

        userPersistenceMapper.updateEntityFromDomain(domainWithEmptyEmail, entityToUpdate);

        assertEquals("New Name", entityToUpdate.getName());
        assertEquals("original@email.com", entityToUpdate.getEmail());
    }

    @Test
    void toNewEntity_whenDomainIsNull_shouldReturnNull() {
        UserEntity result = userPersistenceMapper.toNewEntity(null);

        assertNull(result);
    }

    @Test
    void toNewEntity_whenDomainIsValid_shouldMapWithoutId() {
        UserEntity result = userPersistenceMapper.toNewEntity(user);
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toNewEntity_whenDomainHasNullId_shouldMapWithoutId() {
        User userWithoutId = User.builder()
                .id(null)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        UserEntity result = userPersistenceMapper.toNewEntity(userWithoutId);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toNewEntity_whenDomainHasNullName_shouldMapWithNullName() {
        User userWithNullName = User.builder()
                .name(null)
                .email(USER_EMAIL)
                .build();

        UserEntity result = userPersistenceMapper.toNewEntity(userWithNullName);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toNewEntity_whenDomainHasEmptyName_shouldMapWithEmptyName() {
        User userWithEmptyName = User.builder()
                .name("")
                .email(USER_EMAIL)
                .build();

        UserEntity result = userPersistenceMapper.toNewEntity(userWithEmptyName);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("", result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void toNewEntity_whenDomainHasNullEmail_shouldMapWithNullEmail() {
        User userWithNullEmail = User.builder()
                .name(USER_NAME)
                .email(null)
                .build();

        UserEntity result = userPersistenceMapper.toNewEntity(userWithNullEmail);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(USER_NAME, result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void toNewEntity_whenDomainHasEmptyEmail_shouldMapWithEmptyEmail() {
        User userWithEmptyEmail = User.builder()
                .name(USER_NAME)
                .email("")
                .build();

        UserEntity result = userPersistenceMapper.toNewEntity(userWithEmptyEmail);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(USER_NAME, result.getName());
        assertEquals("", result.getEmail());
    }

    @Test
    void toDomain_shouldNotModifyOriginalEntity() {
        UserEntity originalEntity = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        userPersistenceMapper.toDomain(originalEntity);

        assertEquals(USER_ID, originalEntity.getId());
        assertEquals(USER_NAME, originalEntity.getName());
        assertEquals(USER_EMAIL, originalEntity.getEmail());
    }

    @Test
    void toEntity_shouldNotModifyOriginalDomain() {
        User originalUser = User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        userPersistenceMapper.toEntity(originalUser);

        assertEquals(USER_ID, originalUser.getId());
        assertEquals(USER_NAME, originalUser.getName());
        assertEquals(USER_EMAIL, originalUser.getEmail());
    }

    @Test
    void toNewEntity_shouldNotModifyOriginalDomain() {
        User originalUser = User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        userPersistenceMapper.toNewEntity(originalUser);

        assertEquals(USER_ID, originalUser.getId());
        assertEquals(USER_NAME, originalUser.getName());
        assertEquals(USER_EMAIL, originalUser.getEmail());
    }
}
