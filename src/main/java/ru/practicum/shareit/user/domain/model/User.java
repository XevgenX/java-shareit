package ru.practicum.shareit.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.domain.model.Model;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Model {
    private Long id;
    private String name;
    private String email;
}
