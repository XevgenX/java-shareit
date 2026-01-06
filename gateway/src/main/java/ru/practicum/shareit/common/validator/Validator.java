package ru.practicum.shareit.common.validator;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.exception.ValidationException;

import java.util.Objects;

@Component
public class Validator {
    public void validate(Long id) {
        if (Objects.isNull(id) || id < 0) {
            throw new ValidationException("id не может быть null");
        }
    }

    public void validate(Object dto) {
        if (Objects.isNull(dto)) {
            throw new ValidationException("dto не может быть null");
        }
    }
}
