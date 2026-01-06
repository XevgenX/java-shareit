package ru.practicum.shareit.user.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john@example.com\"");
    }

    @Test
    void deserializeUserDto() throws Exception {
        String json = "{\n" +
                "                \"id\": 1,\n" +
                "                \"name\": \"John Doe\",\n" +
                "                \"email\": \"john@example.com\"\n" +
                "            }";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertThat(userDto.id()).isEqualTo(1L);
        assertThat(userDto.name()).isEqualTo("John Doe");
        assertThat(userDto.email()).isEqualTo("john@example.com");
    }

    @Test
    void deserializeUserDto_WithoutId() throws Exception {
        String json = "{\n" +
                "                \"name\": \"John Doe\",\n" +
                "                \"email\": \"john@example.com\"\n" +
                "            }";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertThat(userDto.id()).isNull();
        assertThat(userDto.name()).isEqualTo("John Doe");
        assertThat(userDto.email()).isEqualTo("john@example.com");
    }

    @Test
    void serializeUserDto_WithNullFields() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .build();

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"id\":null");
        assertThat(json).contains("\"email\":null");
    }
}
