package ru.practicum.shareit.request.persistence.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;
import ru.practicum.shareit.item.persistence.mapper.ItemPersistenceMapper;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.persistence.entity.RequestEntity;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.entity.UserEntity;
import ru.practicum.shareit.user.persistence.mapper.UserPersistenceMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestPersistenceMapperTest {
    private static final Long REQUEST_ID = 1L;
    private static final String DESCRIPTION = "Нужна дрель";

    @Mock
    private UserPersistenceMapper userMapper;

    @Mock
    private ItemPersistenceMapper itemMapper;

    @InjectMocks
    private RequestPersistenceMapper requestPersistenceMapper;

    private ItemRequest itemRequest;
    private RequestEntity requestEntity;
    private User requester;
    private UserEntity requesterEntity;
    private Item item;
    private ItemEntity itemEntity;

    private final LocalDateTime createdDate = LocalDateTime.now().minusDays(1);

    @BeforeEach
    void setUp() {
        requester = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        requesterEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная дрель")
                .build();

        itemEntity = ItemEntity.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная дрель")
                .build();

        itemRequest = ItemRequest.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requester)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        requestEntity = RequestEntity.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requesterEntity)
                .created(createdDate)
                .items(Arrays.asList(itemEntity))
                .build();
    }

    @Test
    void toDomain_whenEntityIsNull_shouldReturnNull() {
        ItemRequest result = requestPersistenceMapper.toDomain(null);

        assertNull(result);
    }

    @Test
    void toDomain_whenEntityIsValid_shouldMapCorrectly() {
        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));

        ItemRequest result = requestPersistenceMapper.toDomain(requestEntity);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(requester, result.getRequester());
        assertEquals(createdDate, result.getCreated());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(item, result.getItems().get(0));

        verify(userMapper).toDomain(requesterEntity);
        verify(itemMapper).toDomainList(Arrays.asList(itemEntity));
    }

    @Test
    void toDomain_whenEntityHasNullRequester_shouldMapWithNullRequester() {
        RequestEntity entityWithNullRequester = RequestEntity.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(null)
                .created(createdDate)
                .items(Arrays.asList(itemEntity))
                .build();

        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));

        ItemRequest result = requestPersistenceMapper.toDomain(entityWithNullRequester);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertNull(result.getRequester());
        assertEquals(createdDate, result.getCreated());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());

        verify(userMapper, never()).toDomain(any());
        verify(itemMapper).toDomainList(Arrays.asList(itemEntity));
    }

    @Test
    void toDomain_whenEntityHasEmptyItems_shouldMapWithEmptyItems() {
        RequestEntity entityWithEmptyItems = RequestEntity.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requesterEntity)
                .created(createdDate)
                .items(Collections.emptyList())
                .build();

        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ItemRequest result = requestPersistenceMapper.toDomain(entityWithEmptyItems);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(requester, result.getRequester());
        assertEquals(createdDate, result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        verify(userMapper).toDomain(requesterEntity);
        verify(itemMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void toDomain_whenEntityHasNullItems_shouldMapWithNullItems() {
        RequestEntity entityWithNullItems = RequestEntity.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requesterEntity)
                .created(createdDate)
                .items(null)
                .build();

        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(null)).thenReturn(Collections.emptyList());

        ItemRequest result = requestPersistenceMapper.toDomain(entityWithNullItems);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(requester, result.getRequester());
        assertEquals(createdDate, result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        verify(userMapper).toDomain(requesterEntity);
        verify(itemMapper).toDomainList(null);
    }

    @Test
    void toDomain_whenEntityHasNullDescription_shouldMapWithNullDescription() {
        RequestEntity entityWithNullDescription = RequestEntity.builder()
                .id(REQUEST_ID)
                .description(null)
                .requester(requesterEntity)
                .created(createdDate)
                .items(Arrays.asList(itemEntity))
                .build();

        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));

        ItemRequest result = requestPersistenceMapper.toDomain(entityWithNullDescription);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertNull(result.getDescription());
        assertEquals(requester, result.getRequester());
        assertEquals(createdDate, result.getCreated());
        assertNotNull(result.getItems());

        verify(userMapper).toDomain(requesterEntity);
        verify(itemMapper).toDomainList(Arrays.asList(itemEntity));
    }

    @Test
    void toDomain_whenEntityHasNullCreated_shouldMapWithNullCreated() {
        RequestEntity entityWithNullCreated = RequestEntity.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requesterEntity)
                .created(null)
                .items(Arrays.asList(itemEntity))
                .build();

        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));

        ItemRequest result = requestPersistenceMapper.toDomain(entityWithNullCreated);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(requester, result.getRequester());
        assertNull(result.getCreated());
        assertNotNull(result.getItems());

        verify(userMapper).toDomain(requesterEntity);
        verify(itemMapper).toDomainList(Arrays.asList(itemEntity));
    }

    @Test
    void toEntity_whenDomainIsNull_shouldReturnNull() {
        RequestEntity result = requestPersistenceMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void toEntity_whenDomainIsValid_shouldMapCorrectly() {
        RequestEntity result = requestPersistenceMapper.toEntity(itemRequest);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertNotNull(result.getRequester());
        assertEquals(requester.getId(), result.getRequester().getId());
        assertEquals(createdDate, result.getCreated());
        assertNull(result.getItems());
    }

    @Test
    void toEntity_whenDomainHasNullRequester_shouldMapWithNullRequester() {
        ItemRequest requestWithNullRequester = ItemRequest.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(null)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        RequestEntity result = requestPersistenceMapper.toEntity(requestWithNullRequester);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertNull(result.getRequester());
        assertEquals(createdDate, result.getCreated());
        assertNull(result.getItems());
    }

    @Test
    void toEntity_whenDomainHasNullId_shouldMapWithNullId() {
        ItemRequest requestWithNullId = ItemRequest.builder()
                .id(null)
                .description(DESCRIPTION)
                .requester(requester)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        RequestEntity result = requestPersistenceMapper.toEntity(requestWithNullId);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertNotNull(result.getRequester());
        assertEquals(requester.getId(), result.getRequester().getId());
        assertEquals(createdDate, result.getCreated());
    }

    @Test
    void toEntity_whenDomainHasRequesterWithNullId_shouldMapWithRequesterHavingNullId() {
        User requesterWithoutId = User.builder()
                .id(null)
                .name("John Doe")
                .email("john@example.com")
                .build();

        ItemRequest requestWithRequesterWithoutId = ItemRequest.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requesterWithoutId)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        RequestEntity result = requestPersistenceMapper.toEntity(requestWithRequesterWithoutId);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertNotNull(result.getRequester());
        assertNull(result.getRequester().getId());
        assertEquals(createdDate, result.getCreated());
    }

    @Test
    void toNewEntity_whenDomainIsNull_shouldReturnNull() {
        RequestEntity result = requestPersistenceMapper.toNewEntity(null, requesterEntity);

        assertNull(result);
    }

    @Test
    void toNewEntity_whenRequesterEntityIsNull_shouldReturnNull() {
        RequestEntity result = requestPersistenceMapper.toNewEntity(itemRequest, null);

        assertNull(result);
    }

    @Test
    void toNewEntity_whenBothParamsAreNull_shouldReturnNull() {
        RequestEntity result = requestPersistenceMapper.toNewEntity(null, null);

        assertNull(result);
    }

    @Test
    void toNewEntity_whenDomainIsValidAndHasCreatedDate_shouldMapCorrectly() {
        RequestEntity result = requestPersistenceMapper.toNewEntity(itemRequest, requesterEntity);

        assertNotNull(result);
        assertNull(result.getId()); // Новый entity не должен иметь ID
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(requesterEntity, result.getRequester());
        assertEquals(createdDate, result.getCreated()); // Используется существующая дата
        assertNull(result.getItems()); // Items не должны маппиться
    }

    @Test
    void toNewEntity_whenDomainHasNullCreated_shouldUseCurrentTime() {
        ItemRequest requestWithoutCreated = ItemRequest.builder()
                .description(DESCRIPTION)
                .requester(requester)
                .created(null)
                .items(Arrays.asList(item))
                .build();

        RequestEntity result = requestPersistenceMapper.toNewEntity(requestWithoutCreated, requesterEntity);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(requesterEntity, result.getRequester());
        assertNotNull(result.getCreated());
        assertNull(result.getItems());
    }

    @Test
    void toNewEntity_whenDomainHasNullDescription_shouldMapWithNullDescription() {
        ItemRequest requestWithNullDescription = ItemRequest.builder()
                .description(null)
                .requester(requester)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        RequestEntity result = requestPersistenceMapper.toNewEntity(requestWithNullDescription, requesterEntity);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getDescription());
        assertEquals(requesterEntity, result.getRequester());
        assertEquals(createdDate, result.getCreated());
        assertNull(result.getItems());
    }

    @Test
    void toNewEntity_shouldNotIncludeIdFromDomain() {
        ItemRequest requestWithId = ItemRequest.builder()
                .id(999L) // Этот ID не должен быть использован
                .description(DESCRIPTION)
                .requester(requester)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        RequestEntity result = requestPersistenceMapper.toNewEntity(requestWithId, requesterEntity);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(requesterEntity, result.getRequester());
        assertEquals(createdDate, result.getCreated());
    }

    @Test
    void toDomainList_whenEntitiesIsNull_shouldReturnEmptyList() {
        List<ItemRequest> result = requestPersistenceMapper.toDomainList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomainList_whenEntitiesIsEmpty_shouldReturnEmptyList() {
        List<ItemRequest> result = requestPersistenceMapper.toDomainList(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomainList_whenEntitiesHasSingleItem_shouldMapCorrectly() {
        List<RequestEntity> entities = Collections.singletonList(requestEntity);
        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));

        List<ItemRequest> result = requestPersistenceMapper.toDomainList(entities);

        assertNotNull(result);
        assertEquals(1, result.size());

        ItemRequest mappedRequest = result.get(0);
        assertEquals(REQUEST_ID, mappedRequest.getId());
        assertEquals(DESCRIPTION, mappedRequest.getDescription());
        assertEquals(requester, mappedRequest.getRequester());
        assertEquals(createdDate, mappedRequest.getCreated());
        assertNotNull(mappedRequest.getItems());
        assertEquals(1, mappedRequest.getItems().size());

        verify(userMapper).toDomain(requesterEntity);
        verify(itemMapper).toDomainList(Arrays.asList(itemEntity));
    }

    @Test
    void toDomainList_whenEntitiesHasMultipleItems_shouldMapAllItems() {
        RequestEntity entity2 = RequestEntity.builder()
                .id(2L)
                .description("Нужен молоток")
                .requester(requesterEntity)
                .created(createdDate.plusDays(1))
                .items(Collections.emptyList())
                .build();

        List<RequestEntity> entities = Arrays.asList(requestEntity, entity2);

        when(userMapper.toDomain(requesterEntity))
                .thenReturn(requester) // Первый вызов
                .thenReturn(requester); // Второй вызов

        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));
        when(itemMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ItemRequest> result = requestPersistenceMapper.toDomainList(entities);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(REQUEST_ID, result.get(0).getId());
        assertEquals(DESCRIPTION, result.get(0).getDescription());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Нужен молоток", result.get(1).getDescription());

        verify(userMapper, times(2)).toDomain(requesterEntity);
        verify(itemMapper).toDomainList(Arrays.asList(itemEntity));
        verify(itemMapper).toDomainList(Collections.emptyList());
    }


    @Test
    void toDomain_shouldNotModifyOriginalEntity() {
        RequestEntity originalEntity = RequestEntity.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requesterEntity)
                .created(createdDate)
                .items(Arrays.asList(itemEntity))
                .build();

        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));

        requestPersistenceMapper.toDomain(originalEntity);

        assertEquals(REQUEST_ID, originalEntity.getId());
        assertEquals(DESCRIPTION, originalEntity.getDescription());
        assertEquals(requesterEntity, originalEntity.getRequester());
        assertEquals(createdDate, originalEntity.getCreated());
        assertEquals(Arrays.asList(itemEntity), originalEntity.getItems());
    }

    @Test
    void toEntity_shouldNotModifyOriginalDomain() {
        ItemRequest originalRequest = ItemRequest.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requester)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        requestPersistenceMapper.toEntity(originalRequest);

        assertEquals(REQUEST_ID, originalRequest.getId());
        assertEquals(DESCRIPTION, originalRequest.getDescription());
        assertEquals(requester, originalRequest.getRequester());
        assertEquals(createdDate, originalRequest.getCreated());
        assertEquals(Arrays.asList(item), originalRequest.getItems());
    }

    @Test
    void toNewEntity_shouldNotModifyOriginalDomain() {
        ItemRequest originalRequest = ItemRequest.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .requester(requester)
                .created(createdDate)
                .items(Arrays.asList(item))
                .build();

        requestPersistenceMapper.toNewEntity(originalRequest, requesterEntity);

        assertEquals(REQUEST_ID, originalRequest.getId());
        assertEquals(DESCRIPTION, originalRequest.getDescription());
        assertEquals(requester, originalRequest.getRequester());
        assertEquals(createdDate, originalRequest.getCreated());
        assertEquals(Arrays.asList(item), originalRequest.getItems());
    }

    @Test
    void toNewEntity_shouldNotModifyRequesterEntity() {
        UserEntity originalRequesterEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        requestPersistenceMapper.toNewEntity(itemRequest, originalRequesterEntity);

        assertEquals(1L, originalRequesterEntity.getId());
        assertEquals("John Doe", originalRequesterEntity.getName());
        assertEquals("john@example.com", originalRequesterEntity.getEmail());
    }

    @Test
    void toDomainList_shouldReturnNewListInstance() {
        List<RequestEntity> entities = Collections.singletonList(requestEntity);
        when(userMapper.toDomain(requesterEntity)).thenReturn(requester);
        when(itemMapper.toDomainList(Arrays.asList(itemEntity))).thenReturn(Arrays.asList(item));

        List<ItemRequest> result = requestPersistenceMapper.toDomainList(entities);

        assertNotNull(result);
        assertNotSame(entities, result); // Должен быть новый список
        assertEquals(1, result.size());
    }
}
