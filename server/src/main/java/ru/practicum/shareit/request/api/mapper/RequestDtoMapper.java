package ru.practicum.shareit.request.api.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.api.mapper.ItemApiMapper;
import ru.practicum.shareit.request.api.dto.ItemRequestDto;
import ru.practicum.shareit.request.api.dto.NewRequestDto;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.user.api.mapper.UserApiMapper;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestDtoMapper {
    private final UserApiMapper userMapper;
    private final ItemApiMapper itemMapper;

    public ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requester(userMapper.toDto(request.getRequester()))
                .items(itemMapper.toDtos(request.getItems()))
                .build();
    }

    public List<ItemRequestDto> toDtos(List<ItemRequest> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ItemRequest toModel(NewRequestDto dto, User user) {
        return ItemRequest.builder()
                .description(dto.description())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
    }
}