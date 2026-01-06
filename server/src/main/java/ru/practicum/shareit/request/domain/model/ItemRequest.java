package ru.practicum.shareit.request.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.domain.model.Model;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest extends Model {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
    private List<Item> items;
}
