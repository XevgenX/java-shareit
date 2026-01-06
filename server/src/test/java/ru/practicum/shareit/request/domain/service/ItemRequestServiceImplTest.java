package ru.practicum.shareit.request.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.domain.repo.ItemRequestRepo;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepo itemRequestRepo;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User testUser;
    private ItemRequest validItemRequest;
    private ItemRequest existingItemRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        validItemRequest = ItemRequest.builder()
                .id(null) // Новый запрос
                .description("Need a power drill")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build();

        existingItemRequest = ItemRequest.builder()
                .id(1L) // Существующий запрос
                .description("Existing request")
                .requester(testUser)
                .created(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void findById_ExistingId_ReturnsItemRequest() {
        when(itemRequestRepo.findById(1L)).thenReturn(Optional.of(existingItemRequest));

        ItemRequest result = itemRequestService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Existing request", result.getDescription());
        verify(itemRequestRepo, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistentId_ThrowsNotFoundException() {
        when(itemRequestRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(999L));
        assertEquals("Не найдено", exception.getMessage());
        verify(itemRequestRepo, times(1)).findById(999L);
    }

    @Test
    void findById_NullId_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.findById(null));
        assertEquals("Некорректный id", exception.getMessage());
        verify(itemRequestRepo, never()).findById(anyLong());
    }

    @Test
    void save_NewItemRequest_ValidatesAndCreates() {
        when(itemRequestRepo.create(any(ItemRequest.class))).thenReturn(validItemRequest);

        ItemRequest result = itemRequestService.save(validItemRequest);

        assertNotNull(result);
        assertEquals("Need a power drill", result.getDescription());
        verify(itemRequestRepo, times(1)).create(validItemRequest);
        verify(itemRequestRepo, never()).update(any(ItemRequest.class));
    }

    @Test
    void save_ExistingItemRequest_ValidatesAndUpdates() {
        ItemRequest updatedRequest = ItemRequest.builder()
                .id(1L)
                .description("Updated description")
                .requester(testUser)
                .created(existingItemRequest.getCreated())
                .build();

        when(itemRequestRepo.update(any(ItemRequest.class))).thenReturn(updatedRequest);

        ItemRequest result = itemRequestService.save(updatedRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated description", result.getDescription());
        verify(itemRequestRepo, times(1)).update(updatedRequest);
        verify(itemRequestRepo, never()).create(any(ItemRequest.class));
    }

    @Test
    void save_NullItemRequest_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.save(null));
        assertEquals("Некорректный item", exception.getMessage());
        verify(itemRequestRepo, never()).create(any(ItemRequest.class));
        verify(itemRequestRepo, never()).update(any(ItemRequest.class));
    }

    @Test
    void save_NewItemRequestWithNullDescription_ValidatesBeforeCreate() {
        ItemRequest invalidRequest = ItemRequest.builder()
                .id(null)
                .description(null) // null описание
                .requester(testUser)
                .created(LocalDateTime.now())
                .build();

        when(itemRequestRepo.create(any(ItemRequest.class))).thenReturn(invalidRequest);

        ItemRequest result = itemRequestService.save(invalidRequest);

        assertNotNull(result);
        verify(itemRequestRepo, times(1)).create(invalidRequest);
    }

    @Test
    void deleteById_ValidId_DeletesSuccessfully() {
        doNothing().when(itemRequestRepo).deleteById(1L);

        itemRequestService.deleteById(1L);

        verify(itemRequestRepo, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_NullId_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.deleteById(null));
        assertEquals("Некорректный id", exception.getMessage());
        verify(itemRequestRepo, never()).deleteById(anyLong());
    }

    @Test
    void findByRequester_ValidRequesterId_ReturnsRequests() {
        List<ItemRequest> requests = List.of(
                ItemRequest.builder()
                        .id(1L)
                        .description("Request 1")
                        .requester(testUser)
                        .created(LocalDateTime.now().minusDays(2))
                        .build(),
                ItemRequest.builder()
                        .id(2L)
                        .description("Request 2")
                        .requester(testUser)
                        .created(LocalDateTime.now().minusDays(1))
                        .build()
        );

        when(itemRequestRepo.findByRequester(1L)).thenReturn(requests);

        List<ItemRequest> result = itemRequestService.findByRequester(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Request 1", result.get(0).getDescription());
        assertEquals("Request 2", result.get(1).getDescription());
        verify(itemRequestRepo, times(1)).findByRequester(1L);
    }

    @Test
    void findByRequester_NonExistentRequesterId_ReturnsEmptyList() {
        when(itemRequestRepo.findByRequester(999L)).thenReturn(List.of());

        List<ItemRequest> result = itemRequestService.findByRequester(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRequestRepo, times(1)).findByRequester(999L);
    }

    @Test
    void findByRequester_MultipleRequesters_ReturnsCorrectRequests() {
        User user2 = User.builder()
                .id(2L)
                .name("User 2")
                .email("user2@example.com")
                .build();

        ItemRequest requestForUser1 = ItemRequest.builder()
                .id(1L)
                .description("Request for user 1")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build();

        ItemRequest requestForUser2 = ItemRequest.builder()
                .id(2L)
                .description("Request for user 2")
                .requester(user2)
                .created(LocalDateTime.now())
                .build();

        when(itemRequestRepo.findByRequester(1L)).thenReturn(List.of(requestForUser1));
        when(itemRequestRepo.findByRequester(2L)).thenReturn(List.of(requestForUser2));

        List<ItemRequest> result1 = itemRequestService.findByRequester(1L);
        List<ItemRequest> result2 = itemRequestService.findByRequester(2L);

        assertEquals(1, result1.size());
        assertEquals("Request for user 1", result1.get(0).getDescription());
        assertEquals(testUser.getId(), result1.get(0).getRequester().getId());

        assertEquals(1, result2.size());
        assertEquals("Request for user 2", result2.get(0).getDescription());
        assertEquals(user2.getId(), result2.get(0).getRequester().getId());
    }

    @Test
    void save_ValidatesBeforeCreateCalledForNewItemRequest() {
        ItemRequest newRequest = ItemRequest.builder()
                .id(null)
                .description("New request")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build();

        when(itemRequestRepo.create(any(ItemRequest.class))).thenReturn(newRequest);

        itemRequestService.save(newRequest);

        verify(itemRequestRepo, times(1)).create(newRequest);
    }

    @Test
    void save_ValidatesBeforePatchCalledForExistingItemRequest() {
        ItemRequest existingRequest = ItemRequest.builder()
                .id(1L)
                .description("Existing request to update")
                .requester(testUser)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        when(itemRequestRepo.update(any(ItemRequest.class))).thenReturn(existingRequest);

        itemRequestService.save(existingRequest);

        verify(itemRequestRepo, times(1)).update(existingRequest);
    }

    @Test
    void save_ItemRequestWithItems_ReturnsWithItems() {
        // Arrange
        Item item = Item.builder()
                .id(1L)
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .build();

        ItemRequest requestWithItems = ItemRequest.builder()
                .id(1L)
                .description("Request with items")
                .requester(testUser)
                .created(LocalDateTime.now())
                .items(List.of(item))
                .build();

        when(itemRequestRepo.update(any(ItemRequest.class))).thenReturn(requestWithItems);

        ItemRequest result = itemRequestService.save(requestWithItems);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Hammer", result.getItems().get(0).getName());
    }

    @Test
    void findByRequester_RequestsSortedByCreatedDate() {
        ItemRequest oldRequest = ItemRequest.builder()
                .id(1L)
                .description("Old request")
                .requester(testUser)
                .created(LocalDateTime.now().minusDays(3))
                .build();

        ItemRequest newRequest = ItemRequest.builder()
                .id(2L)
                .description("New request")
                .requester(testUser)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        List<ItemRequest> requests = List.of(oldRequest, newRequest);
        when(itemRequestRepo.findByRequester(1L)).thenReturn(requests);

        List<ItemRequest> result = itemRequestService.findByRequester(1L);

        assertEquals(2, result.size());
        assertEquals("Old request", result.get(0).getDescription());
        assertEquals("New request", result.get(1).getDescription());
    }
}
