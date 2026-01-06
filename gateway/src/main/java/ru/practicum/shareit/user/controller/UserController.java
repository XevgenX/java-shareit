package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validator.Validator;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient client;
    private final Validator validator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        validator.validate(id);
        return client.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto user) {
        validator.validate(user);
        return client.create(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody @Valid UserDto user) {
        validator.validate(id);
        validator.validate(user);
        return client.update(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable long id) {
        validator.validate(id);
        return client.delete(id);
    }
}
