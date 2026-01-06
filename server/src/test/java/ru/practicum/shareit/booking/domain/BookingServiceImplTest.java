package ru.practicum.shareit.booking.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.booking.domain.repo.BookingRepo;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepo bookingRepo;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item availableItem;
    private Item unavailableItem;
    private Booking booking;
    private Booking futureBooking;
    private Booking pastBooking;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Booker")
                .email("booker@example.com")
                .build();

        availableItem = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(owner)
                .build();

        unavailableItem = Item.builder()
                .id(2L)
                .name("Hammer")
                .description("Heavy hammer")
                .available(false) // Недоступна для бронирования
                .owner(owner)
                .build();

        LocalDateTime now = LocalDateTime.now();
        booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        futureBooking = Booking.builder()
                .id(2L)
                .start(now.plusDays(5))
                .end(now.plusDays(7))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        pastBooking = Booking.builder()
                .id(3L)
                .start(now.minusDays(5))
                .end(now.minusDays(3))
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void approve_ValidApprovalByOwner_Success() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepo.update(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.approve(1L, owner, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepo, times(1)).findById(1L);
        verify(bookingRepo, times(1)).update(booking);
    }

    @Test
    void approve_ValidRejectionByOwner_Success() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepo.update(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.approve(1L, owner, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepo, times(1)).findById(1L);
        verify(bookingRepo, times(1)).update(booking);
    }

    @Test
    void approve_NonExistentBooking_ThrowsValidationException() {
        when(bookingRepo.findById(999L)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approve(999L, owner, true));
        assertEquals("Booking not existed", exception.getMessage());
        verify(bookingRepo, times(1)).findById(999L);
        verify(bookingRepo, never()).update(any(Booking.class));
    }

    @Test
    void approve_ByNonOwner_ThrowsValidationException() {
        User anotherUser = User.builder()
                .id(3L)
                .name("Another User")
                .email("another@example.com")
                .build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approve(1L, anotherUser, true));
        assertEquals("Cannot approve item without ownership", exception.getMessage());
        verify(bookingRepo, times(1)).findById(1L);
        verify(bookingRepo, never()).update(any(Booking.class));
    }

    @Test
    void approve_ByBooker_ThrowsValidationException() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approve(1L, booker, true));
        assertEquals("Cannot approve item without ownership", exception.getMessage());
        verify(bookingRepo, times(1)).findById(1L);
        verify(bookingRepo, never()).update(any(Booking.class));
    }

    @Test
    void approve_AlreadyApprovedBooking_CanReject() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepo.update(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.approve(1L, owner, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepo, times(1)).findById(1L);
        verify(bookingRepo, times(1)).update(booking);
    }

    @Test
    void findByBooker_WithoutStatus_ReturnsAllBookings() {
        List<Booking> bookings = List.of(booking, futureBooking, pastBooking);
        when(bookingRepo.findByBooker(booker)).thenReturn(bookings);

        List<Booking> result = bookingService.findByBooker(booker, Optional.empty());

        assertEquals(3, result.size());
        verify(bookingRepo, times(1)).findByBooker(booker);
        verify(bookingRepo, never()).findByBookerAndState(any(), any());
    }

    @Test
    void findByBooker_WithStatus_ReturnsFilteredBookings() {
        List<Booking> waitingBookings = List.of(booking, futureBooking);
        when(bookingRepo.findByBookerAndState(booker, BookingStatus.WAITING)).thenReturn(waitingBookings);

        List<Booking> result = bookingService.findByBooker(booker, Optional.of(BookingStatus.WAITING));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getStatus() == BookingStatus.WAITING));
        verify(bookingRepo, times(1)).findByBookerAndState(booker, BookingStatus.WAITING);
        verify(bookingRepo, never()).findByBooker(any());
    }

    @Test
    void findByOwnerShip_WithoutStatus_ReturnsAllOwnerBookings() {
        List<Booking> ownerBookings = List.of(booking, futureBooking);
        when(bookingRepo.findByOwnerShip(owner)).thenReturn(ownerBookings);

        List<Booking> result = bookingService.findByOwnerShip(owner, Optional.empty());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getItem().getOwner().getId().equals(owner.getId())));
        verify(bookingRepo, times(1)).findByOwnerShip(owner);
        verify(bookingRepo, never()).findByOwnerShipAndState(any(), any());
    }

    @Test
    void findByOwnerShip_WithStatus_ReturnsFilteredOwnerBookings() {
        List<Booking> waitingOwnerBookings = List.of(booking);
        when(bookingRepo.findByOwnerShipAndState(owner, BookingStatus.WAITING)).thenReturn(waitingOwnerBookings);

        List<Booking> result = bookingService.findByOwnerShip(owner, Optional.of(BookingStatus.WAITING));

        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
        verify(bookingRepo, times(1)).findByOwnerShipAndState(owner, BookingStatus.WAITING);
        verify(bookingRepo, never()).findByOwnerShip(any());
    }

    @Test
    void findByItemId_ValidItemId_ReturnsBookings() {
        List<Booking> itemBookings = List.of(booking, pastBooking);
        when(bookingRepo.findByItemId(1L)).thenReturn(itemBookings);

        List<Booking> result = bookingService.findByItemId(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getItem().getId().equals(1L)));
        verify(bookingRepo, times(1)).findByItemId(1L);
    }

    @Test
    void findByItemId_ItemWithoutBookings_ReturnsEmptyList() {
        when(bookingRepo.findByItemId(999L)).thenReturn(List.of());

        List<Booking> result = bookingService.findByItemId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepo, times(1)).findByItemId(999L);
    }

    @Test
    void save_NewBookingWithAvailableItem_Success() {
        booking.setId(null); // Новое бронирование
        when(bookingRepo.create(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.save(booking);

        assertNotNull(result);
        verify(bookingRepo, times(1)).create(booking);
        verify(bookingRepo, never()).update(any(Booking.class));
    }

    @Test
    void save_UpdateExistingBooking_Success() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepo.update(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.save(booking);

        assertNotNull(result);
        verify(bookingRepo, times(1)).update(booking);
        verify(bookingRepo, never()).create(any(Booking.class));
    }

    @Test
    void save_BookingWithUnavailableItem_ThrowsValidationException() {
        Booking invalidBooking = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(unavailableItem) // Недоступный предмет
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.save(invalidBooking));
        assertEquals("Cannot book unavailable item", exception.getMessage());
        verify(bookingRepo, never()).create(any(Booking.class));
        verify(bookingRepo, never()).update(any(Booking.class));
    }

    @Test
    void save_NullBooking_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.save(null));
        assertEquals("Некорректный item", exception.getMessage());
        verify(bookingRepo, never()).create(any(Booking.class));
        verify(bookingRepo, never()).update(any(Booking.class));
    }

    @Test
    void findById_ExistingBooking_ReturnsBooking() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepo, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistentBooking_ThrowsNotFoundException() {
        when(bookingRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(999L));
        assertEquals("Не найдено", exception.getMessage());
        verify(bookingRepo, times(1)).findById(999L);
    }

    @Test
    void deleteById_ValidId_DeletesSuccessfully() {
        doNothing().when(bookingRepo).deleteById(1L);

        bookingService.deleteById(1L);

        verify(bookingRepo, times(1)).deleteById(1L);
    }

    @Test
    void findByBooker_WithDifferentStatuses_ReturnsCorrectResults() {
        List<Booking> approvedBookings = List.of(pastBooking);
        List<Booking> rejectedBookings = List.of();
        List<Booking> waitingBookings = List.of(booking, futureBooking);

        when(bookingRepo.findByBookerAndState(booker, BookingStatus.APPROVED)).thenReturn(approvedBookings);
        when(bookingRepo.findByBookerAndState(booker, BookingStatus.REJECTED)).thenReturn(rejectedBookings);
        when(bookingRepo.findByBookerAndState(booker, BookingStatus.WAITING)).thenReturn(waitingBookings);

        List<Booking> approvedResult = bookingService.findByBooker(booker, Optional.of(BookingStatus.APPROVED));
        assertEquals(1, approvedResult.size());
        assertEquals(BookingStatus.APPROVED, approvedResult.get(0).getStatus());

        List<Booking> rejectedResult = bookingService.findByBooker(booker, Optional.of(BookingStatus.REJECTED));
        assertTrue(rejectedResult.isEmpty());

        List<Booking> waitingResult = bookingService.findByBooker(booker, Optional.of(BookingStatus.WAITING));
        assertEquals(2, waitingResult.size());
        assertTrue(waitingResult.stream().allMatch(b -> b.getStatus() == BookingStatus.WAITING));
    }

    @Test
    void findByOwnerShip_MultipleItems_ReturnsAllItemsBookings() {
        Item item2 = Item.builder()
                .id(2L)
                .name("Saw")
                .description("Circular saw")
                .available(true)
                .owner(owner)
                .build();

        Booking booking2 = Booking.builder()
                .id(4L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item2)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        List<Booking> allBookings = List.of(booking, booking2);
        when(bookingRepo.findByOwnerShip(owner)).thenReturn(allBookings);

        List<Booking> result = bookingService.findByOwnerShip(owner, Optional.empty());

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .map(b -> b.getItem().getId())
                .allMatch(id -> id == 1L || id == 2L));
    }

    @Test
    void findByBooker_UserWithoutBookings_ReturnsEmptyList() {
        User newUser = User.builder()
                .id(99L)
                .name("New User")
                .email("new@example.com")
                .build();

        when(bookingRepo.findByBooker(newUser)).thenReturn(List.of());

        List<Booking> result = bookingService.findByBooker(newUser, Optional.empty());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_BookingWithNullItem_ThrowsNullPointerException() {
        Booking invalidBooking = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(null) // null item
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        assertThrows(NullPointerException.class,
                () -> bookingService.save(invalidBooking));
    }

    @Test
    void approve_BookingAlreadyInSameStatus_ReturnsSameStatus() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepo.update(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.approve(1L, owner, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepo, times(1)).findById(1L);
        verify(bookingRepo, times(1)).update(booking);
    }

    @Test
    void findByItemId_MultipleBookingsForSameItem_ReturnsAll() {
        List<Booking> allBookings = List.of(booking, futureBooking, pastBooking);
        when(bookingRepo.findByItemId(1L)).thenReturn(allBookings);

        List<Booking> result = bookingService.findByItemId(1L);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(b -> b.getItem().getId().equals(1L)));
    }
}
