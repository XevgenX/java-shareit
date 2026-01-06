package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.validator.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingClient bookingClient;

    @Mock
    private Validator validator;

    @InjectMocks
    private BookingController bookingController;

    private final Long validUserId = 1L;
    private final Long validBookingId = 10L;
    private final boolean approved = true;

    private NewBookingDto createValidBookingDto() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        return new NewBookingDto(100L, start, end);
    }

    @Test
    void create_ValidData_ReturnsResponseFromClient() {
        NewBookingDto validDto = createValidBookingDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Object());

        doNothing().when(validator).validate(validDto);
        doNothing().when(validator).validate(validUserId);
        when(bookingClient.create(validDto, validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.create(validDto, validUserId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(validator, times(1)).validate(validDto);
        verify(validator, times(1)).validate(validUserId);
        verify(bookingClient, times(1)).create(validDto, validUserId);
    }

    @Test
    void create_InvalidDates_ThrowsValidationException() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        LocalDateTime futureDate = LocalDateTime.now().plusDays(2);
        NewBookingDto invalidDto = new NewBookingDto(100L, pastDate, futureDate);

        doNothing().when(validator).validate(invalidDto);
        doNothing().when(validator).validate(validUserId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingController.create(invalidDto, validUserId));
        assertTrue(exception.getMessage().contains("cannot be in the Past"));

        verify(validator, times(1)).validate(invalidDto);
        verify(validator, times(1)).validate(validUserId);
        verify(bookingClient, never()).create(any(), anyLong());
    }

    @Test
    void create_EndBeforeStart_ThrowsValidationException() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        NewBookingDto invalidDto = new NewBookingDto(100L, start, end);

        doNothing().when(validator).validate(invalidDto);
        doNothing().when(validator).validate(validUserId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingController.create(invalidDto, validUserId));
        assertEquals("End date cannot be before Start", exception.getMessage());
    }

    @Test
    void create_EndEqualsStart_ThrowsValidationException() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        NewBookingDto invalidDto = new NewBookingDto(100L, sameTime, sameTime);

        doNothing().when(validator).validate(invalidDto);
        doNothing().when(validator).validate(validUserId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingController.create(invalidDto, validUserId));
        assertEquals("End date cannot equals to Start", exception.getMessage());
    }

    @Test
    void approve_ValidData_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());

        doNothing().when(validator).validate(validBookingId);
        doNothing().when(validator).validate(validUserId);
        when(bookingClient.approve(validBookingId, approved, validUserId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.approve(validBookingId, approved, validUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validBookingId);
        verify(validator, times(1)).validate(validUserId);
        verify(bookingClient, times(1)).approve(validBookingId, approved, validUserId);
    }

    @Test
    void findById_ValidId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        doNothing().when(validator).validate(validBookingId);
        when(bookingClient.findById(validBookingId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.findById(validBookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validBookingId);
        verify(bookingClient, times(1)).findById(validBookingId);
    }

    @Test
    void findAll_WithUserId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        Optional<BookingStatus> status = Optional.of(BookingStatus.WAITING);

        when(bookingClient.findAll(validUserId, status)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.findAll(validUserId, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, never()).validate(validUserId);
        verify(bookingClient, times(1)).findAll(validUserId, status);
    }

    @Test
    void findAll_WithoutStatus_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());

        when(bookingClient.findAll(validUserId, Optional.empty())).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.findAll(validUserId, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingClient, times(1)).findAll(validUserId, Optional.empty());
    }

    @Test
    void findByOwner_ValidUserId_ReturnsResponseFromClient() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(new Object());
        Optional<BookingStatus> status = Optional.of(BookingStatus.APPROVED);

        doNothing().when(validator).validate(validUserId);
        when(bookingClient.findByOwnerId(validUserId, status)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.findByOwner(validUserId, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator, times(1)).validate(validUserId);
        verify(bookingClient, times(1)).findByOwnerId(validUserId, status);
    }

    @Test
    void create_WithBoundaryDates_ValidatesCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusSeconds(5); // Меньше 10 секунд
        LocalDateTime end = now.plusDays(1);
        NewBookingDto dto = new NewBookingDto(100L, start, end);

        doNothing().when(validator).validate(dto);
        doNothing().when(validator).validate(validUserId);

        when(bookingClient.create(dto, validUserId)).thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> bookingController.create(dto, validUserId));
    }

    @Test
    void approve_InvalidBookingId_ThrowsValidationException() {
        Long invalidBookingId = -1L;
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(invalidBookingId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingController.approve(invalidBookingId, approved, validUserId));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(invalidBookingId);
        verify(validator, never()).validate(validUserId);
        verify(bookingClient, never()).approve(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void findByOwner_InvalidUserId_ThrowsValidationException() {
        Long invalidUserId = 0L;
        doThrow(new ValidationException("id не может быть null"))
                .when(validator).validate(invalidUserId);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingController.findByOwner(invalidUserId, Optional.empty()));
        assertEquals("id не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(invalidUserId);
        verify(bookingClient, never()).findByOwnerId(anyLong(), any());
    }

    @Test
    void create_NullBookingDto_ThrowsValidationException() {
        NewBookingDto nullDto = null;
        doThrow(new ValidationException("dto не может быть null"))
                .when(validator).validate(nullDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingController.create(nullDto, validUserId));
        assertEquals("dto не может быть null", exception.getMessage());
        verify(validator, times(1)).validate(nullDto);
        verify(validator, never()).validate(validUserId);
        verify(bookingClient, never()).create(any(), anyLong());
    }

    @Test
    void findAll_ClientReturnsEmptyList_ReturnsOkWithEmptyList() {
        ResponseEntity<Object> emptyResponse = ResponseEntity.ok(List.of());
        when(bookingClient.findAll(validUserId, Optional.empty())).thenReturn(emptyResponse);

        ResponseEntity<Object> response = bookingController.findAll(validUserId, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    void create_OrderOfCalls_CorrectOrder() {
        NewBookingDto validDto = createValidBookingDto();

        doNothing().when(validator).validate(validDto);
        doNothing().when(validator).validate(validUserId);
        when(bookingClient.create(validDto, validUserId)).thenReturn(ResponseEntity.ok().build());

        bookingController.create(validDto, validUserId);

        var inOrder = inOrder(validator, bookingClient);

        inOrder.verify(validator).validate(validDto);
        inOrder.verify(validator).validate(validUserId);
        inOrder.verify(bookingClient).create(validDto, validUserId);
    }
}
