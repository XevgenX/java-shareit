package ru.practicum.shareit.booking.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JsonTest
class NewBookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeNewBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 17, 18, 0);

        NewBookingDto dto = new NewBookingDto(1L, start, end);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"2024-01-15T10:00:00\"");
        assertThat(json).contains("\"end\":\"2024-01-17T18:00:00\"");
    }

    @Test
    void deserializeNewBookingDto() throws Exception {
        String json = """
            {
                "itemId": 1,
                "start": "2024-01-15T10:00:00",
                "end": "2024-01-17T18:00:00"
            }
            """;

        NewBookingDto dto = objectMapper.readValue(json, NewBookingDto.class);

        assertThat(dto.itemId()).isEqualTo(1L);
        assertThat(dto.start()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
        assertThat(dto.end()).isEqualTo(LocalDateTime.of(2024, 1, 17, 18, 0));
    }

    @Test
    void deserializeNewBookingDto_InvalidDateFormats() throws Exception {
        String invalidJson = """
            {
                "itemId": 1,
                "start": "invalid-date",
                "end": "2024-01-17T18:00:00"
            }
            """;

        assertThatThrownBy(() -> objectMapper.readValue(invalidJson, NewBookingDto.class))
                .isInstanceOf(com.fasterxml.jackson.databind.exc.InvalidFormatException.class);
    }
}
