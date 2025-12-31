package ru.practicum.shareit.comment.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;
import ru.practicum.shareit.user.persistence.entity.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(name = "created", nullable = false)
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}
