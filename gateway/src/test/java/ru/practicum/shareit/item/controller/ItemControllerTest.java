package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.validator.Validator;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @Mock
    private Validator validator;

    @InjectMocks
    private ItemController itemController;

    private final Long validUserId = 1L;
    private final Long validItemId = 10L;
    private final String validSearchText = "drill";
    private final ItemDto validItemDto = ItemDto.builder()
            .name("Drill")
            .description("Power drill")
            .available(true)
            .build();
    private final NewCommentDto validCommentDto = new NewCommentDto("Great item!");

    @Test
    void findByUserId_ValidUserId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(validUserId);
        when(itemClient.findByUserId(validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.findByUserId(validUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validUserId);
        verify(itemClient, times(1)).findByUserId(validUserId);
    }

    @Test
    void findById_ValidItemId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(validItemId);
        when(itemClient.findById(validItemId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.findById(validItemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validItemId);
        verify(itemClient, times(1)).findById(validItemId);
    }

    @Test
    void findCommentById_ValidItemId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(validItemId);
        when(itemClient.findCommentById(validItemId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.findCommentById(validItemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validItemId);
        verify(itemClient, times(1)).findCommentById(validItemId);
    }

    @Test
    void search_ValidText_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(validSearchText);
        when(itemClient.search(validSearchText)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.search(validSearchText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validSearchText);
        verify(itemClient, times(1)).search(validSearchText);
    }

    @Test
    void search_EmptyText_ValidatesAndReturnsResponse() {
        String emptyText = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(emptyText);
        when(itemClient.search(emptyText)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.search(emptyText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(emptyText);
        verify(itemClient, times(1)).search(emptyText);
    }

    @Test
    void create_ValidData_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Object());

        doNothing().when(validator).validate(validItemDto);
        doNothing().when(validator).validate(validUserId);
        when(itemClient.create(validItemDto, validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.create(validItemDto, validUserId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(validator, times(1)).validate(validItemDto);
        verify(validator, times(1)).validate(validUserId);
        verify(itemClient, times(1)).create(validItemDto, validUserId);
    }

    @Test
    void addComment_ValidData_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());

        doNothing().when(validator).validate(validItemId);
        doNothing().when(validator).validate(validCommentDto);
        doNothing().when(validator).validate(validUserId);
        when(itemClient.addComment(validItemId, validCommentDto, validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.addCmment(validItemId, validCommentDto, validUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validItemId);
        verify(validator, times(1)).validate(validCommentDto);
        verify(validator, times(1)).validate(validUserId);
        verify(itemClient, times(1)).addComment(validItemId, validCommentDto, validUserId);
    }

    @Test
    void update_ValidData_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());

        doNothing().when(validator).validate(validItemId);
        doNothing().when(validator).validate(validUserId);
        doNothing().when(validator).validate(validItemDto);
        when(itemClient.update(validItemId, validItemDto, validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.update(validItemId, validItemDto, validUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validItemId);
        verify(validator, times(1)).validate(validUserId);
        verify(validator, times(1)).validate(validItemDto);
        verify(itemClient, times(1)).update(validItemId, validItemDto, validUserId);
    }

    @Test
    void update_InvalidItemId_ThrowsValidationException() {
        Long invalidItemId = -1L;
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(invalidItemId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemController.update(invalidItemId, validItemDto, validUserId));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(invalidItemId);
        verify(validator, never()).validate(validUserId);
        verify(validator, never()).validate(validItemDto);
        verify(itemClient, never()).update(anyLong(), any(), anyLong());
    }

    @Test
    void addComment_InvalidCommentDto_ThrowsValidationException() {
        NewCommentDto invalidCommentDto = null;
        doNothing().when(validator).validate(validItemId);
        doThrow(new ValidationException("dto не может быть null"))
                .when(validator).validate(invalidCommentDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemController.addCmment(validItemId, invalidCommentDto, validUserId));
        assertEquals("dto не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(validItemId);
        verify(validator, times(1)).validate(invalidCommentDto);
        verify(validator, never()).validate(validUserId);
        verify(itemClient, never()).addComment(anyLong(), any(), anyLong());
    }

    @Test
    void findByUserId_ClientReturnsNotFound_PropagatesResponse() {
        ResponseEntity<Object> notFoundResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found");

        doNothing().when(validator).validate(validUserId);
        when(itemClient.findByUserId(validUserId)).thenReturn(notFoundResponse);

        ResponseEntity<Object> response = itemController.findByUserId(validUserId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void update_OrderOfValidation_CorrectOrder() {
        doNothing().when(validator).validate(validItemId);
        doNothing().when(validator).validate(validUserId);
        doNothing().when(validator).validate(validItemDto);

        when(itemClient.update(validItemId, validItemDto, validUserId))
                .thenReturn(ResponseEntity.ok().build());

        itemController.update(validItemId, validItemDto, validUserId);

        var inOrder = inOrder(validator, itemClient);

        inOrder.verify(validator).validate(validItemId);
        inOrder.verify(validator).validate(validUserId);
        inOrder.verify(validator).validate(validItemDto);
        inOrder.verify(itemClient).update(validItemId, validItemDto, validUserId);
    }
}
