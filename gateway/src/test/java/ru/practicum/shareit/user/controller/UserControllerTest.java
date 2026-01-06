package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.validator.Validator;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserClient userClient;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserController userController;

    private final UserDto validUserDto = UserDto.builder()
            .id(1L)
            .name("John Doe")
            .email("john@example.com")
            .build();

    @Test
    void findById_ValidId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(validUserDto);
        doNothing().when(validator).validate(1L);
        when(userClient.findById(1L)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validUserDto, response.getBody());
        verify(validator, times(1)).validate(1L);
        verify(userClient, times(1)).findById(1L);
    }

    @Test
    void findById_NegativeId_ThrowsValidationException() {
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(-1L);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.findById(-1L));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(-1L);
        verify(userClient, never()).findById(anyLong());
    }

    @Test
    void create_ValidUser_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(validUserDto);

        doNothing().when(validator).validate(validUserDto);
        when(userClient.create(validUserDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.create(validUserDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(validUserDto, response.getBody());
        verify(validator, times(1)).validate(validUserDto);
        verify(userClient, times(1)).create(validUserDto);
    }

    @Test
    void update_ValidIdAndUser_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(validUserDto);
        doNothing().when(validator).validate(1L);
        doNothing().when(validator).validate(validUserDto);
        when(userClient.update(1L, validUserDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.update(1L, validUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validUserDto, response.getBody());
        verify(validator, times(1)).validate(1L);
        verify(validator, times(1)).validate(validUserDto);
        verify(userClient, times(1)).update(1L, validUserDto);
    }

    @Test
    void update_NegativeId_ThrowsValidationException() {
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(-1L);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.update(-1L, validUserDto));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(-1L);
        verify(validator, never()).validate(validUserDto);
        verify(userClient, never()).update(anyLong(), any());
    }

    @Test
    void deleteById_ValidId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
        doNothing().when(validator).validate(1L);
        when(userClient.delete(1L)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.deleteById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(validator, times(1)).validate(1L);
        verify(userClient, times(1)).delete(1L);
    }

    @Test
    void deleteById_ZeroId_ThrowsValidationException() {
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(0L);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.deleteById(0L));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(0L);
        verify(userClient, never()).delete(anyLong());
    }

    @Test
    void findById_ClientReturnsError_ReturnsErrorResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found");

        doNothing().when(validator).validate(999L);
        when(userClient.findById(999L)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.findById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(validator, times(1)).validate(999L);
        verify(userClient, times(1)).findById(999L);
    }

    @Test
    void create_ClientReturnsError_ReturnsErrorResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("User already exists");

        doNothing().when(validator).validate(validUserDto);
        when(userClient.create(validUserDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.create(validUserDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
        verify(validator, times(1)).validate(validUserDto);
        verify(userClient, times(1)).create(validUserDto);
    }

    @Test
    void update_ClientReturnsError_ReturnsErrorResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found for update");

        doNothing().when(validator).validate(999L);
        doNothing().when(validator).validate(validUserDto);
        when(userClient.update(999L, validUserDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.update(999L, validUserDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found for update", response.getBody());
        verify(validator, times(1)).validate(999L);
        verify(validator, times(1)).validate(validUserDto);
        verify(userClient, times(1)).update(999L, validUserDto);
    }

    @Test
    void deleteById_ClientReturnsError_ReturnsErrorResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found for deletion");

        doNothing().when(validator).validate(999L);
        when(userClient.delete(999L)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.deleteById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found for deletion", response.getBody());
        verify(validator, times(1)).validate(999L);
        verify(userClient, times(1)).delete(999L);
    }

    @Test
    void findById_ValidatorThrowsDifferentException_PropagatesException() {
        doThrow(new IllegalArgumentException("Invalid ID format"))
                .when(validator).validate(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userController.findById(1L));
        assertEquals("Invalid ID format", exception.getMessage());
        verify(validator, times(1)).validate(1L);
        verify(userClient, never()).findById(anyLong());
    }

    @Test
    void update_PartialUpdateWithNullFields_ValidatesOnlyId() {
        UserDto partialUpdate = UserDto.builder()
                .name("Only name updated")
                .email(null)
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(partialUpdate);

        doNothing().when(validator).validate(1L);
        doNothing().when(validator).validate(partialUpdate);
        when(userClient.update(1L, partialUpdate)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.update(1L, partialUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(1L);
        verify(validator, times(1)).validate(partialUpdate);
        verify(userClient, times(1)).update(1L, partialUpdate);
    }

    @Test
    void findById_WithZeroId_ThrowsValidationException() {
        doNothing().when(validator).validate(0L);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        when(userClient.findById(0L)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.findById(0L);

        assertNotNull(response);
    }

    @Test
    void findById_WithMaxLongValue_ReturnsResponse() {
        long maxId = Long.MAX_VALUE;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(validUserDto);

        doNothing().when(validator).validate(maxId);
        when(userClient.findById(maxId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.findById(maxId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(maxId);
        verify(userClient, times(1)).findById(maxId);
    }

    @Test
    void allMethods_VerifyCorrectOrderOfCalls() {
        doNothing().when(validator).validate(1L);
        doNothing().when(validator).validate(validUserDto);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(validUserDto);
        when(userClient.update(1L, validUserDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.update(1L, validUserDto);

        var inOrder = inOrder(validator, userClient);

        inOrder.verify(validator).validate(1L);
        inOrder.verify(validator).validate(validUserDto);
        inOrder.verify(userClient).update(1L, validUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
