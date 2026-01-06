package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.validator.Validator;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.NewRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestClient requestClient;

    @Mock
    private Validator validator;

    @InjectMocks
    private RequestController requestController;

    private final NewRequestDto validRequestDto = new NewRequestDto("Need a power drill");
    private final Long validUserId = 1L;
    private final Long validRequestId = 10L;

    @Test
    void create_ValidData_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Object());

        doNothing().when(validator).validate(validUserId);
        doNothing().when(validator).validate(validRequestDto);
        when(requestClient.create(validRequestDto, validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestController.create(validRequestDto, validUserId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(validator, times(1)).validate(validUserId);
        verify(validator, times(1)).validate(validRequestDto);
        verify(requestClient, times(1)).create(validRequestDto, validUserId);
    }

    @Test
    void create_InvalidUserId_ThrowsValidationException() {
        Long invalidUserId = -1L;
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(invalidUserId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> requestController.create(validRequestDto, invalidUserId));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(invalidUserId);
        verify(validator, never()).validate(validRequestDto);
        verify(requestClient, never()).create(any(), anyLong());
    }

    @Test
    void findById_ValidId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(validRequestId);
        when(requestClient.findById(validRequestId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestController.findById(validRequestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validRequestId);
        verify(requestClient, times(1)).findById(validRequestId);
    }

    @Test
    void findAll_ValidUserId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(validUserId);
        when(requestClient.findByRequesterId(validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestController.findAll(validUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validUserId);
        verify(requestClient, times(1)).findByRequesterId(validUserId);
    }

    @Test
    void findAll_InvalidUserId_ThrowsValidationException() {
        Long invalidUserId = 0L;
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(invalidUserId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> requestController.findAll(invalidUserId));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(invalidUserId);
        verify(requestClient, never()).findByRequesterId(anyLong());
    }

    @Test
    void create_ClientReturnsError_PropagatesError() {
        ResponseEntity<Object> errorResponse = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid request data");

        doNothing().when(validator).validate(validUserId);
        doNothing().when(validator).validate(validRequestDto);
        when(requestClient.create(validRequestDto, validUserId)).thenReturn(errorResponse);

        ResponseEntity<Object> response = requestController.create(validRequestDto, validUserId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request data", response.getBody());
    }
}
