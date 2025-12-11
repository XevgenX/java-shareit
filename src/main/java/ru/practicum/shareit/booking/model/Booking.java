package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;

    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;
}

