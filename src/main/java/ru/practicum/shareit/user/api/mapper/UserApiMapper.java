package ru.practicum.shareit.user.api.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserApiMapper {
    public User toModel(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .id(userDto.id())
                .name(userDto.name())
                .email(userDto.email())
                .build();
    }

    // Преобразование списка DTO в список моделей
    public List<User> toModels(List<UserDto> userDtos) {
        if (userDtos == null) {
            return null;
        }

        return userDtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    // Преобразование модели в DTO
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    // Преобразование списка моделей в список DTO
    public List<UserDto> toDtos(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
