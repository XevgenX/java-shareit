package ru.practicum.shareit.comment.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewCommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeNewCommentDto() throws Exception {
        NewCommentDto dto = new NewCommentDto("Great item!");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"Great item!\"");
    }

    @Test
    void deserializeNewCommentDto() throws Exception {
        String json = """
            {
                "text": "Excellent quality!"
            }
            """;

        NewCommentDto dto = objectMapper.readValue(json, NewCommentDto.class);

        assertThat(dto.text()).isEqualTo("Excellent quality!");
    }

    @Test
    void deserializeNewCommentDto_EmptyText() throws Exception {
        String json = """
            {
                "text": ""
            }
            """;

        NewCommentDto dto = objectMapper.readValue(json, NewCommentDto.class);

        assertThat(dto.text()).isEmpty();
    }
}
