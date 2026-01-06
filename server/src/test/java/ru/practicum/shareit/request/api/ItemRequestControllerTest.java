package ru.practicum.shareit.request.api;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.domain.ItemService;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.persistence.repo.ItemRepository;
import ru.practicum.shareit.request.api.dto.NewRequestDto;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.domain.service.ItemRequestService;
import ru.practicum.shareit.request.persistence.repo.RequestRepository;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.repo.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RequestRepository requestRepo;

    @Autowired
    private ItemRepository itemRepo;

    private User testUser;
    private User anotherUser;
    private NewRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        itemRepo.deleteAll();
        requestRepo.deleteAll();
        userRepo.deleteAll();

        testUser = userService.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        anotherUser = userService.save(User.builder()
                .name("Another User")
                .email("another@example.com")
                .build());

        validRequestDto = new NewRequestDto("Need a power drill for home repairs");
    }

    @Test
    void createRequest_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Need a power drill for home repairs"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void createRequest_WithNonExistentUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, 999L) // Несуществующий пользователь
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRequest_LongDescription_ReturnsCreated() throws Exception {
        String longDescription = "I need a very specific tool for my woodworking project. " +
                "It should be a high-quality Japanese chisel with a blade width of 24mm, " +
                "made from white steel #2, with a ho wood handle. The chisel should be " +
                "well-sharpened and ready to use for fine joinery work.";

        NewRequestDto longRequest = new NewRequestDto(longDescription);

        mockMvc.perform(post("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(longRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(longDescription));
    }

    @Test
    void findById_ExistingRequest_ReturnsRequest() throws Exception {
        ItemRequest request = requestService.save(ItemRequest.builder()
                .description("Need a hammer")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/requests/{id}", request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value("Need a hammer"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void findById_ExistingRequestWithItems_ReturnsRequestWithItems() throws Exception {
        ItemRequest request = requestService.save(ItemRequest.builder()
                .description("Need tools")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build());

        itemService.save(Item.builder()
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .owner(anotherUser)
                .request(request)
                .build());

        mockMvc.perform(get("/requests/{id}", request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value("Hammer"));
    }

    @Test
    void findById_NonExistentRequest_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/requests/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_UserWithRequests_ReturnsRequests() throws Exception {
        requestService.save(ItemRequest.builder()
                .description("First request")
                .requester(testUser)
                .created(LocalDateTime.now().minusDays(2))
                .build());

        requestService.save(ItemRequest.builder()
                .description("Second request")
                .requester(testUser)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        mockMvc.perform(get("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[1].description").exists())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
    }

    @Test
    void findAll_UserWithoutRequests_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, anotherUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAll_ReturnsRequestsWithItems() throws Exception {
        ItemRequest request1 = requestService.save(ItemRequest.builder()
                .description("Request with items")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build());

        requestService.save(ItemRequest.builder()
                .description("Request without items")
                .requester(testUser)
                .created(LocalDateTime.now().minusHours(1))
                .build());

        itemService.save(Item.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(anotherUser)
                .request(request1)
                .build());

        itemService.save(Item.builder()
                .name("Screwdriver Set")
                .description("Various screwdrivers")
                .available(false)
                .owner(anotherUser)
                .request(request1)
                .build());

        mockMvc.perform(get("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].items", hasSize(2))) // Первый запрос имеет 2 items
                .andExpect(jsonPath("$[1].items", hasSize(0))); // Второй запрос без items
    }

    @Test
    void createMultipleRequests_DifferentUsers() throws Exception {
        NewRequestDto request1 = new NewRequestDto("First user request");
        NewRequestDto request2 = new NewRequestDto("Second user request");

        mockMvc.perform(post("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, anotherUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("First user request"));

        mockMvc.perform(get("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, anotherUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Second user request"));
    }

    @Test
    void findById_RequestFromDifferentUser_ReturnsRequest() throws Exception {
        ItemRequest request = requestService.save(ItemRequest.builder()
                .description("Private request")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/requests/{id}", request.getId()))
                .andExpect(status().isOk()) // Или другой код, в зависимости от политики доступа
                .andExpect(jsonPath("$.description").value("Private request"));
    }

    @Test
    void createRequest_MinimalDescription_ReturnsCreated() throws Exception {
        NewRequestDto minimalRequest = new NewRequestDto("a"); // Минимальная длина

        mockMvc.perform(post("/requests")
                        .header(ItemRequestController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("a"));
    }

    @Test
    void findById_RequestWithSpecialCharacters_ReturnsRequest() throws Exception {
        String specialDescription = "Need tool for project: монтаж + демонтаж (спец. работы)";
        ItemRequest request = requestService.save(ItemRequest.builder()
                .description(specialDescription)
                .requester(testUser)
                .created(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/requests/{id}", request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(specialDescription));
    }
}
