package ru.practicum.shareit.item.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.mapper.ItemMapper;
import ru.practicum.shareit.item.domain.ItemService;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;

import java.net.URI;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper mapper;

    @GetMapping
    public ResponseEntity<List<ItemDto>> findById(@RequestHeader(USER_ID_HEADER) Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(mapper.toDtos(itemService.findByOwner(user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findById(@PathVariable long id) {
        return ResponseEntity.ok(mapper.toDto(itemService.findById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        return ResponseEntity.ok(mapper.toDtos(itemService.findByTextContainsInNameAndDescription(text)));
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody @Valid ItemDto dto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        User user = userService.findById(userId);
        Item item = mapper.toModel(dto);
        item.setOwner(user);
        Item created = itemService.save(item);
        return ResponseEntity
                .created(URI.create("/users/" + created.getId()))
                .body(mapper.toDto(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@PathVariable long id,
                                          @RequestBody @Valid ItemDto dto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        User user = userService.findById(userId);
        Item item = mapper.toModel(dto);
        item.setId(id);
        item.setOwner(user);
        Item created = itemService.save(item);
        return ResponseEntity.ok(mapper.toDto(created));
    }
}
