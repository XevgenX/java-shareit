package ru.practicum.shareit.item.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.domain.BookingService;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.comment.api.dto.CommentDto;
import ru.practicum.shareit.comment.api.dto.NewCommentDto;
import ru.practicum.shareit.comment.api.mapper.CommentApiMapper;
import ru.practicum.shareit.comment.domain.CommentService;
import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemExtendedDto;
import ru.practicum.shareit.item.api.mapper.ItemApiMapper;
import ru.practicum.shareit.item.domain.ItemService;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.request.domain.service.ItemRequestService;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;
    private final CommentService commentService;
    private final BookingService bookingService;
    private final ItemRequestService requestService;
    private final ItemApiMapper mapper;
    private final CommentApiMapper commentMapper;

    @GetMapping
    public ResponseEntity<List<ItemDto>> findByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(mapper.toDtos(itemService.findByOwner(user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemExtendedDto> findById(@PathVariable long id) {
        Item item = itemService.findById(id);

        List<Comment> comments = commentService.findByItemId(item.getId());
        List<Booking> bookings = bookingService.findByItemId(id);
        Booking lastBooking = bookings.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now().minusSeconds(5)))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        LocalDateTime lastBookingDate = lastBooking != null ? lastBooking.getEnd() : null;
        LocalDateTime nextBookingDate = nextBooking != null ? nextBooking.getStart() : null;

        ItemExtendedDto dto = ItemExtendedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentMapper.toDtos(comments))
                .lastBooking(lastBookingDate)
                .nextBooking(nextBookingDate)
                .build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<ItemDto> findCommentById(@PathVariable long id) {
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
        if (Objects.nonNull(dto.requestId())) {
            item.setRequest(requestService.findById(dto.requestId()));
        }
        Item created = itemService.save(item);
        return ResponseEntity
                .created(URI.create("/users/" + created.getId()))
                .body(mapper.toDto(created));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDto> addCmment(@PathVariable long id,
                                                @RequestBody NewCommentDto newComment,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        Comment comment = Comment.builder()
                .item(itemService.findById(id))
                .text(newComment.text())
                .author(userService.findById(userId))
                .created(LocalDateTime.now())
                .build();
        Comment saved = commentService.save(comment);
        saved.setItem(itemService.findById(id));
        saved.setAuthor(userService.findById(userId));
        return ResponseEntity.ok(commentMapper.toDto(saved));
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
