package ru.practicum.shareit.booking.api.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.NewBookingDto;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.item.api.mapper.ItemApiMapper;
import ru.practicum.shareit.user.api.mapper.UserApiMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingApiMapper {
    private final ItemApiMapper itemMapper;
    private final UserApiMapper userMapper;

    public Booking toModel(BookingDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        return Booking.builder()
                .id(itemDto.id())
                .start(itemDto.start())
                .end(itemDto.end())
                .item(itemMapper.toModel(itemDto.item()))
                .booker(userMapper.toModel(itemDto.booker()))
                .status(itemDto.status())
                .created(itemDto.created())
                .build();
    }

    public Booking toModel(NewBookingDto item) {
        if (item == null) {
            return null;
        }
        return Booking.builder()
                .start(item.start())
                .end(item.end())
                .build();
    }

    public List<Booking> toModels(List<BookingDto> itemDtos) {
        if (itemDtos == null) {
            return null;
        }
        return itemDtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public BookingDto toDto(Booking item) {
        if (item == null) {
            return null;
        }
        return BookingDto.builder()
                .id(item.getId())
                .start(item.getStart())
                .end(item.getEnd())
                .item(itemMapper.toDto(item.getItem()))
                .booker(userMapper.toDto(item.getBooker()))
                .status(item.getStatus())
                .created(item.getCreated())
                .build();
    }

    public List<BookingDto> toDtos(List<Booking> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
