package ru.practicum.shareit.item.api.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.domain.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public Item toModel(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        return Item.builder()
                .id(itemDto.id())
                .name(itemDto.name())
                .description(itemDto.description())
                .available(itemDto.available())
                .build();
    }

    public List<Item> toModels(List<ItemDto> itemDtos) {
        if (itemDtos == null) {
            return null;
        }
        return itemDtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public List<ItemDto> toDtos(List<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
