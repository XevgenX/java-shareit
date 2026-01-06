package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validator.Validator;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient client;
    private final Validator validator;

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(userId);
        return client.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        validator.validate(id);
        return client.findById(id);
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<Object> findCommentById(@PathVariable long id) {
        validator.validate(id);
        return client.findCommentById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        validator.validate(text);
        return client.search(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemDto dto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(dto);
        validator.validate(userId);
        return client.create(dto, userId);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addCmment(@PathVariable long id,
                                                @RequestBody NewCommentDto newComment,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(id);
        validator.validate(newComment);
        validator.validate(userId);
        return client.addComment(id, newComment, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id,
                                          @RequestBody @Valid ItemDto dto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        validator.validate(id);
        validator.validate(userId);
        validator.validate(dto);
        return client.update(id, dto, userId);
    }
}
