package ru.practicum.shareit.booking.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.user.api.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeBookingDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();

        UserDto bookerDto = UserDto.builder()
                .id(2L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(10L)
                .start(LocalDateTime.of(2024, 1, 15, 10, 0))
                .end(LocalDateTime.of(2024, 1, 17, 18, 0))
                .status(BookingStatus.APPROVED)
                .item(itemDto)
                .booker(bookerDto)
                .build();

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":10");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"item\"");
        assertThat(json).contains("\"booker\"");
        assertThat(json).contains("2024-01-15T10:00:00");
        assertThat(json).contains("2024-01-17T18:00:00");
    }

    @Test
    void deserializeBookingDto() throws Exception {
        String json = """
            {
                "id": 10,
                "start": "2024-01-15T10:00:00",
                "end": "2024-01-17T18:00:00",
                "status": "WAITING",
                "item": {
                    "id": 1,
                    "name": "Drill",
                    "description": "Power drill",
                    "available": true
                },
                "booker": {
                    "id": 2,
                    "name": "John Doe",
                    "email": "john@example.com"
                }
            }
            """;

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.id()).isEqualTo(10L);
        assertThat(bookingDto.status()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingDto.item().name()).isEqualTo("Drill");
        assertThat(bookingDto.booker().name()).isEqualTo("John Doe");
        assertThat(bookingDto.start()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
        assertThat(bookingDto.end()).isEqualTo(LocalDateTime.of(2024, 1, 17, 18, 0));
    }
}
