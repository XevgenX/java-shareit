package ru.practicum.shareit.booking.api;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.api.dto.NewBookingDto;
import ru.practicum.shareit.booking.domain.BookingService;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.booking.persistence.repo.BookingRepository;
import ru.practicum.shareit.item.domain.ItemService;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.item.persistence.repo.ItemRepository;
import ru.practicum.shareit.user.domain.UserService;
import ru.practicum.shareit.user.domain.model.User;
import ru.practicum.shareit.user.persistence.repo.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private BookingRepository bookingRepo;

    private User owner;
    private User booker;
    private Item availableItem;
    private NewBookingDto validBookingDto;
    private NewBookingDto invalidBookingDto;

    @BeforeEach
    void setUp() {
        // Настройка ObjectMapper для работы с LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        // Очистка базы данных перед каждым тестом
        bookingRepo.deleteAll();
        itemRepo.deleteAll();
        userRepo.deleteAll();

        // Создание тестовых пользователей
        owner = userService.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        booker = userService.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        // Создание доступного предмета
        availableItem = itemService.save(Item.builder()
                .name("Power Drill")
                .description("Electric power drill")
                .available(true)
                .owner(owner)
                .build());

        // Подготовка тестовых DTO
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);

        validBookingDto = new NewBookingDto(
                availableItem.getId(),
                start,
                end
        );

        invalidBookingDto = new NewBookingDto(
                availableItem.getId(),
                LocalDateTime.now().minusDays(1), // Прошедшая дата начала
                LocalDateTime.now().plusDays(1)
        );
    }

    @Test
    void createBooking_ValidData_ReturnsCreated() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/bookings")
                        .header(BookingController.USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(availableItem.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()));
    }

    @Test
    void createBooking_NonExistentItem_ReturnsNotFound() throws Exception {
        // Arrange
        NewBookingDto nonExistentItemDto = new NewBookingDto(
                999L, // Несуществующий предмет
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );

        // Act & Assert
        mockMvc.perform(post("/bookings")
                        .header(BookingController.USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentItemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findBookingById_ExistingBooking_ReturnsBooking() throws Exception {
        // Arrange
        Booking booking = bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        // Act & Assert
        mockMvc.perform(get("/bookings/{id}", booking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(availableItem.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()));
    }

    @Test
    void findBookingById_NonExistentBooking_ReturnsNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/bookings/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllBookings_ByBooker_WithoutStatus() throws Exception {
        // Arrange - создаем несколько бронирований
        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(7))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        // Act & Assert
        mockMvc.perform(get("/bookings")
                        .header(BookingController.USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].booker.id").value(booker.getId()))
                .andExpect(jsonPath("$[1].booker.id").value(booker.getId()));
    }

    @Test
    void findAllBookings_ByBooker_WithStatusFilter() throws Exception {
        // Arrange
        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(7))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        // Act & Assert - фильтр WAITING
        mockMvc.perform(get("/bookings")
                        .header(BookingController.USER_ID_HEADER, booker.getId())
                        .param("status", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

        // Act & Assert - фильтр APPROVED
        mockMvc.perform(get("/bookings")
                        .header(BookingController.USER_ID_HEADER, booker.getId())
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void findAllBookings_ByBooker_NoBookings_ReturnsEmptyList() throws Exception {
        // Arrange - создаем нового пользователя без бронирований
        User newUser = userService.save(User.builder()
                .name("New User")
                .email("new@example.com")
                .build());

        // Act & Assert
        mockMvc.perform(get("/bookings")
                        .header(BookingController.USER_ID_HEADER, newUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findByOwner_WithStatusFilter() throws Exception {
        // Arrange
        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(7))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        // Act & Assert - фильтр WAITING
        mockMvc.perform(get("/bookings/owner")
                        .header(BookingController.USER_ID_HEADER, owner.getId())
                        .param("status", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void findByOwner_OwnerWithoutItems_ReturnsEmptyList() throws Exception {
        // Arrange - создаем владельца без предметов
        User ownerWithoutItems = userService.save(User.builder()
                .name("Owner without items")
                .email("empty@example.com")
                .build());

        // Act & Assert
        mockMvc.perform(get("/bookings/owner")
                        .header(BookingController.USER_ID_HEADER, ownerWithoutItems.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findByOwner_MultipleItems_ReturnsAllBookings() throws Exception {
        // Arrange - создаем второй предмет для владельца
        Item secondItem = itemService.save(Item.builder()
                .name("Saw")
                .description("Circular saw")
                .available(true)
                .owner(owner)
                .build());

        // Создаем бронирования для обоих предметов
        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        bookingService.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(7))
                .item(secondItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        // Act & Assert
        mockMvc.perform(get("/bookings/owner")
                        .header(BookingController.USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void createBooking_WithZeroDuration_ReturnsBadRequest() throws Exception {
        // Arrange
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        NewBookingDto zeroDurationDto = new NewBookingDto(
                availableItem.getId(),
                sameTime,
                sameTime
        );

        // Act & Assert
        mockMvc.perform(post("/bookings")
                        .header(BookingController.USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zeroDurationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllBookings_PaginationNotSupported_ReturnsAll() throws Exception {
        // Arrange - создаем 3 бронирования
        for (int i = 1; i <= 3; i++) {
            bookingService.save(Booking.builder()
                    .start(LocalDateTime.now().plusDays(i))
                    .end(LocalDateTime.now().plusDays(i + 2))
                    .item(availableItem)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build());
        }

        // Act & Assert
        mockMvc.perform(get("/bookings")
                        .header(BookingController.USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
