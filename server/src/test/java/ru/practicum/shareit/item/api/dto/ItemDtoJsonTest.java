package ru.practicum.shareit.item.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(10L)
                .build();

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"description\":\"Power drill\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":10");
    }

    @Test
    void deserializeItemDto() throws Exception {
        String json = """
            {
                "id": 1,
                "name": "Drill",
                "description": "Power drill",
                "available": true,
                "requestId": 10
            }
            """;

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.id()).isEqualTo(1L);
        assertThat(itemDto.name()).isEqualTo("Drill");
        assertThat(itemDto.description()).isEqualTo("Power drill");
        assertThat(itemDto.available()).isTrue();
        assertThat(itemDto.requestId()).isEqualTo(10L);
    }

    @Test
    void deserializeItemDto_WithMissingOptionalFields() throws Exception {
        String json = """
            {
                "name": "Hammer",
                "description": "Heavy hammer",
                "available": false
            }
            """;

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.id()).isNull();
        assertThat(itemDto.name()).isEqualTo("Hammer");
        assertThat(itemDto.description()).isEqualTo("Heavy hammer");
        assertThat(itemDto.available()).isFalse();
        assertThat(itemDto.requestId()).isNull();
    }
}
