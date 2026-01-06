package ru.practicum.shareit.item.api;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.domain.BookingService;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.booking.persistence.repo.BookingRepository;
import ru.practicum.shareit.comment.api.dto.NewCommentDto;
import ru.practicum.shareit.comment.domain.CommentService;
import ru.practicum.shareit.comment.persistence.repo.CommentRepository;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.domain.ItemService;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.persistence.repo.ItemRepository;
import ru.practicum.shareit.request.domain.model.ItemRequest;
import ru.practicum.shareit.request.domain.service.ItemRequestService;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.repo.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private CommentRepository commentRepo;

    private User testUser;
    private User anotherUser;
    private ItemDto validItemDto;
    private NewCommentDto validCommentDto;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        commentRepo.deleteAll();
        bookingRepo.deleteAll();
        itemRepo.deleteAll();
        userRepo.deleteAll();

        testUser = userService.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        anotherUser = userService.save(User.builder()
                .name("Another User")
                .email("another@example.com")
                .build());

        validItemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        validCommentDto = new NewCommentDto("Great item!");
    }

    @Test
    void createItem_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/items")
                        .header(ItemController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createItem_WithRequestId_ReturnsCreated() throws Exception {
        ItemRequest request = requestService.save(ItemRequest.builder()
                .description("Need a drill")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build());

        ItemDto itemWithRequest = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(request.getId())
                .build();

        mockMvc.perform(post("/items")
                        .header(ItemController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void createItem_InvalidData_ReturnsBadRequest() throws Exception {
        ItemDto invalidItem = ItemDto.builder()
                .name("") // Пустое имя
                .description("")
                .available(null)
                .build();
        mockMvc.perform(post("/items")
                        .header(ItemController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByUserId_ValidUser_ReturnsItems() throws Exception {
        itemService.save(Item.builder()
                .name("Item 1")
                .description("Desc 1")
                .available(true)
                .owner(testUser)
                .build());

        itemService.save(Item.builder()
                .name("Item 2")
                .description("Desc 2")
                .available(false)
                .owner(testUser)
                .build());
        mockMvc.perform(get("/items")
                        .header(ItemController.USER_ID_HEADER, testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void findByUserId_UserWithoutItems_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/items")
                        .header(ItemController.USER_ID_HEADER, anotherUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findById_WithBookings_ReturnsItemWithBookingDates() throws Exception {
        Item item = itemService.save(Item.builder()
                .name("Bookable Item")
                .description("Available for booking")
                .available(true)
                .owner(testUser)
                .build());
        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .build());
        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .build());
        mockMvc.perform(get("/items/{id}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists());
    }

    @Test
    void findById_NonExistentItem_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/items/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchItems_WithText_ReturnsMatchingItems() throws Exception {
        itemService.save(Item.builder()
                .name("Drill powerful")
                .description("Electric drill")
                .available(true)
                .owner(testUser)
                .build());

        itemService.save(Item.builder()
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .owner(testUser)
                .build());

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Drill powerful"));
    }

    @Test
    void searchItems_EmptyText_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchItems_NoMatchingItems_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addComment_ValidComment_ReturnsComment() throws Exception {
        Item item = itemService.save(Item.builder()
                .name("Commentable Item")
                .description("Item for comments")
                .available(true)
                .owner(testUser)
                .build());

        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .build());

        mockMvc.perform(post("/items/{id}/comment", item.getId())
                        .header(ItemController.USER_ID_HEADER, anotherUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").exists());
    }

    @Test
    void addComment_WithoutBooking_ReturnsBadRequest() throws Exception {
        Item item = itemService.save(Item.builder()
                .name("No Booking Item")
                .description("Item without booking")
                .available(true)
                .owner(testUser)
                .build());

        mockMvc.perform(post("/items/{id}/comment", item.getId())
                        .header(ItemController.USER_ID_HEADER, anotherUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDto)))
                .andExpect(status().isBadRequest()); // Или другой код ошибки, в зависимости от реализации
    }

    @Test
    void addComment_ByOwner_ReturnsBadRequest() throws Exception {
        Item item = itemService.save(Item.builder()
                .name("Owned Item")
                .description("Owner cannot comment")
                .available(true)
                .owner(testUser)
                .build());

        mockMvc.perform(post("/items/{id}/comment", item.getId())
                        .header(ItemController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ValidUpdate_ReturnsUpdatedItem() throws Exception {
        Item existingItem = itemService.save(Item.builder()
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(testUser)
                .build());

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        mockMvc.perform(patch("/items/{id}", existingItem.getId())
                        .header(ItemController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void updateItem_PartialUpdate_ReturnsUpdatedItem() throws Exception {
        Item existingItem = itemService.save(Item.builder()
                .name("Original Name")
                .description("Original Description")
                .available(true)
                .owner(testUser)
                .build());

        ItemDto partialUpdate = ItemDto.builder()
                .name("Only Name Updated")
                .build();

        mockMvc.perform(patch("/items/{id}", existingItem.getId())
                        .header(ItemController.USER_ID_HEADER, testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Only Name Updated"))
                .andExpect(jsonPath("$.description").value("Original Description")); // Осталось прежним
    }

    @Test
    void findCommentById_ExistingItem_ReturnsItem() throws Exception {
        Item item = itemService.save(Item.builder()
                .name("Item for comment check")
                .description("Test")
                .available(true)
                .owner(testUser)
                .build());
        mockMvc.perform(get("/items/{id}/comment", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value("Item for comment check"));
    }

    @Test
    void findCommentById_NonExistentItem_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/items/{id}/comment", 999L))
                .andExpect(status().isNotFound());
    }
}
