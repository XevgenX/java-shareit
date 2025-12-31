package ru.practicum.shareit.item.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.domain.model.Model;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.domain.model.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends Model {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
