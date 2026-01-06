package ru.practicum.shareit.common.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private final Validator validator = new Validator();

    @Test
    void validate_ValidId_DoesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(1L));
        assertDoesNotThrow(() -> validator.validate(0L)); // 0 тоже валиден?
        assertDoesNotThrow(() -> validator.validate(Long.MAX_VALUE));
    }

    @Test
    void validate_NullId_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate((Long) null));
        assertEquals("id не может быть null", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, -10, -100, Long.MIN_VALUE})
    void validate_NegativeId_ThrowsValidationException(long negativeId) {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(negativeId));
        assertEquals("id не может быть null", exception.getMessage());
    }

    @Test
    void validate_ValidObject_DoesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(new Object()));
        assertDoesNotThrow(() -> validator.validate("String"));
        assertDoesNotThrow(() -> validator.validate(123));
    }

    @Test
    void validate_NullObject_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate((Object) null));
        assertEquals("dto не может быть null", exception.getMessage());
    }

    @Test
    void validate_EmptyStringObject_DoesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(""));
    }

    @Test
    void validate_CustomDtoObject_DoesNotThrow() {
        UserDto userDto = UserDto.builder()
                .name("Test")
                .email("test@example.com")
                .build();

        assertDoesNotThrow(() -> validator.validate(userDto));
    }
}
