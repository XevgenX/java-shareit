package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validator.Validator;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.NewRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient client;
    private final Validator validator;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid NewRequestDto dto,
                                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(userId);
        validator.validate(dto);
        return client.create(dto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        validator.validate(id);
        return client.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(userId);
        return client.findByRequesterId(userId);
    }
}
