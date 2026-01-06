package ru.practicum.shareit.request.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeRecordDto() throws Exception {
        NewRequestDto dto = new NewRequestDto("Need a power drill for home repairs");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"description\":\"Need a power drill for home repairs\"");
    }

    @Test
    void deserializeRecordDto() throws Exception {
        String json = "{\n" +
                "                \"description\": \"Looking for a circular saw\"\n" +
                "            }";

        NewRequestDto dto = objectMapper.readValue(json, NewRequestDto.class);

        assertThat(dto.description()).isEqualTo("Looking for a circular saw");
    }
}
