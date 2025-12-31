package ru.practicum.shareit.request.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.persistence.entity.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
