package ru.practicum.shareit.booking.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.item.persistence.entity.ItemEntity;
import ru.practicum.shareit.user.persistence.entity.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private UserEntity booker;

    @Column(name = "status", columnDefinition = "booking_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;

    @Column(name = "created", nullable = false)
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}
