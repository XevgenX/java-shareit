package ru.practicum.shareit.user.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.mapper.UserApiMapper;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;

import java.net.URI;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserApiMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable long id) {
        return ResponseEntity.ok(mapper.toDto(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto user) {
        User created = service.save(mapper.toModel(user));
        return ResponseEntity
                .created(URI.create("/users/" + created.getId()))
                .body(mapper.toDto(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable long id, @RequestBody @Valid UserDto user) {
        User model = mapper.toModel(user);
        model.setId(id);
        User updated = service.save(model);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
