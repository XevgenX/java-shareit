package ru.practicum.shareit.comment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.domain.model.Model;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends Model {
    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;
}
