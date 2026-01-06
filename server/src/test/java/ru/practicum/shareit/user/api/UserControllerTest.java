package ru.practicum.shareit.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.persistence.repo.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository jpaRepository;

    private UserDto validUserDto;
    private UserDto invalidUserDto;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        jpaRepository.deleteAll();

        // Подготовка тестовых данных
        validUserDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        invalidUserDto = UserDto.builder()
                .name("Invalid User")
                .email("invalid-email") // Некорректный email
                .build();
    }

    @Test
    void createUser_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void createUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_DuplicateEmail_ReturnsConflict() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDto)));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        UserDto emptyUserDto = UserDto.builder().build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() throws Exception {
        // Arrange
        // Создаем пользователя через API
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(response, UserDto.class);
        Long userId = createdUser.id();
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUserById_NonExistentUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ValidData_ReturnsUpdatedUser() throws Exception {
        // Arrange
        // Создаем пользователя
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        Long userId = createdUser.id();
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateUser_PartialUpdate_ReturnsUpdatedUser() throws Exception {
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        Long userId = createdUser.id();
        UserDto partialUpdateDto = UserDto.builder()
                .name("Updated Name Only")
                // Email не передаем - должен сохраниться старый
                .build();
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name Only"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com")); // Старый email сохранился
    }

    @Test
    void updateUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        Long userId = createdUser.id();
        UserDto invalidUpdateDto = UserDto.builder()
                .email("invalid-email-format")
                .build();
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_DuplicateEmail_ReturnsConflict() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDto)));
        UserDto secondUserDto = UserDto.builder()
                .name("Second User")
                .email("second@example.com")
                .build();

        String secondUserResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondUserDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto secondUser = objectMapper.readValue(secondUserResponse, UserDto.class);
        UserDto duplicateEmailDto = UserDto.builder()
                .email("john.doe@example.com") // Email первого пользователя
                .build();
        mockMvc.perform(patch("/users/{id}", secondUser.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUser_NonExistentUser_ReturnsNotFound() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("Non Existent")
                .email("test@example.com")
                .build();
        mockMvc.perform(patch("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ExistingUser_ReturnsNoContent() throws Exception {
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        Long userId = createdUser.id();
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
