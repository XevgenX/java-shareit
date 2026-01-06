package ru.practicum.shareit.booking.api.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.NewBookingDto;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.mapper.ItemApiMapper;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.mapper.UserApiMapper;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingApiMapperTest {
    private static final Long BOOKING_ID = 1L;

    @Mock
    private ItemApiMapper itemMapper;

    @Mock
    private UserApiMapper userMapper;

    @InjectMocks
    private BookingApiMapper bookingApiMapper;

    private BookingDto bookingDto;
    private NewBookingDto newBookingDto;
    private Booking booking;
    private ItemDto itemDto;
    private UserDto userDto;
    private Item item;
    private User user;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .build();

        user = User.builder()
                .id(1L)
                .name("Test User")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .build();

        booking = Booking.builder()
                .id(BOOKING_ID)
                .start(now)
                .end(now.plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .created(now.minusHours(1))
                .build();

        bookingDto = BookingDto.builder()
                .id(BOOKING_ID)
                .start(now)
                .end(now.plusDays(1))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.APPROVED)
                .created(now.minusHours(1))
                .build();

        newBookingDto = new NewBookingDto(item.getId(), now, now.plusDays(1));
    }

    @Test
    void toModel_whenBookingDtoIsNull_shouldReturnNull() {
        Booking result = bookingApiMapper.toModel((BookingDto) null);

        assertNull(result);
    }

    @Test
    void toModel_whenBookingDtoIsValid_shouldMapCorrectly() {
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(userMapper.toModel(userDto)).thenReturn(user);

        Booking result = bookingApiMapper.toModel(bookingDto);

        assertNotNull(result);
        assertEquals(BOOKING_ID, result.getId());
        assertEquals(now, result.getStart());
        assertEquals(now.plusDays(1), result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(user, result.getBooker());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(now.minusHours(1), result.getCreated());

        verify(itemMapper).toModel(itemDto);
        verify(userMapper).toModel(userDto);
    }

    @Test
    void toModel_whenNewBookingDtoIsNull_shouldReturnNull() {
        Booking result = bookingApiMapper.toModel((NewBookingDto) null);

        assertNull(result);
    }

    @Test
    void toModel_whenNewBookingDtoIsValid_shouldMapCorrectly() {
        Booking result = bookingApiMapper.toModel(newBookingDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(now, result.getStart());
        assertEquals(now.plusDays(1), result.getEnd());
        assertNull(result.getItem());
        assertNull(result.getBooker());
    }

    @Test
    void toDto_whenBookingIsNull_shouldReturnNull() {
        BookingDto result = bookingApiMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void toDto_whenBookingIsValid_shouldMapCorrectly() {
        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(userMapper.toDto(user)).thenReturn(userDto);

        BookingDto result = bookingApiMapper.toDto(booking);

        assertNotNull(result);
        assertEquals(BOOKING_ID, result.id());
        assertEquals(now, result.start());
        assertEquals(now.plusDays(1), result.end());
        assertEquals(itemDto, result.item());
        assertEquals(userDto, result.booker());
        assertEquals(BookingStatus.APPROVED, result.status());
        assertEquals(now.minusHours(1), result.created());

        verify(itemMapper).toDto(item);
        verify(userMapper).toDto(user);
    }

    @Test
    void toModels_whenListIsNull_shouldReturnNull() {
        List<Booking> result = bookingApiMapper.toModels(null);

        assertNull(result);
    }

    @Test
    void toModels_whenListIsEmpty_shouldReturnEmptyList() {
        List<Booking> result = bookingApiMapper.toModels(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toModels_whenListHasItems_shouldMapAllItems() {
        List<BookingDto> dtos = Arrays.asList(bookingDto, bookingDto);
        when(itemMapper.toModel(any())).thenReturn(item);
        when(userMapper.toModel(any())).thenReturn(user);

        List<Booking> result = bookingApiMapper.toModels(dtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemMapper, times(2)).toModel(any());
        verify(userMapper, times(2)).toModel(any());
    }

    @Test
    void toDtos_whenListIsNull_shouldReturnNull() {
        List<BookingDto> result = bookingApiMapper.toDtos(null);

        assertNull(result);
    }

    @Test
    void toDtos_whenListIsEmpty_shouldReturnEmptyList() {
        List<BookingDto> result = bookingApiMapper.toDtos(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDtos_whenListHasItems_shouldMapAllItems() {
        List<Booking> bookings = Arrays.asList(booking, booking);
        when(itemMapper.toDto(any())).thenReturn(itemDto);
        when(userMapper.toDto(any())).thenReturn(userDto);

        List<BookingDto> result = bookingApiMapper.toDtos(bookings);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemMapper, times(2)).toDto(any());
        verify(userMapper, times(2)).toDto(any());
    }

    @Test
    void toModel_whenBookingDtoHasNullItem_shouldHandleGracefully() {
        BookingDto dtoWithNullItem = BookingDto.builder()
                .id(BOOKING_ID)
                .start(now)
                .end(now.plusDays(1))
                .item(null)
                .booker(userDto)
                .status(BookingStatus.APPROVED)
                .created(now.minusHours(1))
                .build();

        when(itemMapper.toModel(null)).thenReturn(null);
        when(userMapper.toModel(userDto)).thenReturn(user);

        Booking result = bookingApiMapper.toModel(dtoWithNullItem);

        assertNotNull(result);
        assertNull(result.getItem());
        assertEquals(user, result.getBooker());
    }

    @Test
    void toDto_whenBookingHasNullItem_shouldHandleGracefully() {
        Booking bookingWithNullItem = Booking.builder()
                .id(BOOKING_ID)
                .start(now)
                .end(now.plusDays(1))
                .item(null)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .created(now.minusHours(1))
                .build();

        when(itemMapper.toDto(null)).thenReturn(null);
        when(userMapper.toDto(user)).thenReturn(userDto);

        BookingDto result = bookingApiMapper.toDto(bookingWithNullItem);

        assertNotNull(result);
        assertNull(result.item());
        assertEquals(userDto, result.booker());
    }

    @Test
    void toDto_shouldNotModifyOriginalBooking() {
        Booking originalBooking = Booking.builder()
                .id(BOOKING_ID)
                .start(now)
                .end(now.plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .created(now.minusHours(1))
                .build();

        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(userMapper.toDto(user)).thenReturn(userDto);

        bookingApiMapper.toDto(originalBooking);

        assertEquals(BOOKING_ID, originalBooking.getId());
        assertEquals(now, originalBooking.getStart());
        assertEquals(now.plusDays(1), originalBooking.getEnd());
        assertEquals(item, originalBooking.getItem());
        assertEquals(user, originalBooking.getBooker());
        assertEquals(BookingStatus.APPROVED, originalBooking.getStatus());
        assertEquals(now.minusHours(1), originalBooking.getCreated());
    }
}
