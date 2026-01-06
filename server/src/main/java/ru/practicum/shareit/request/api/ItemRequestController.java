package ru.practicum.shareit.request.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.domain.ItemService;
import ru.practicum.shareit.request.api.dto.ItemRequestDto;
import ru.practicum.shareit.request.api.dto.NewRequestDto;
import ru.practicum.shareit.request.api.mapper.RequestDtoMapper;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.domain.service.ItemRequestService;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;
    private final RequestDtoMapper requestDtoMapper;
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestBody @Valid NewRequestDto dto,
                                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        User user = userService.findById(userId);
        ItemRequest request = requestDtoMapper.toModel(dto, user);
        ItemRequest saved = requestService.save(request);
        return ResponseEntity
                .created(URI.create("/bookings/" + saved.getId()))
                .body(requestDtoMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRequestDto> findById(@PathVariable long id) {
        return ResponseEntity.ok(requestDtoMapper.toDto(requestService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> findAll(@RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(requestDtoMapper.toDtos(requestService.findByRequester(userId)));
    }
}
