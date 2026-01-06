package ru.practicum.shareit.comment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.domain.BookingService;
import ru.practicum.shareit.booking.domain.model.Booking;
import ru.practicum.shareit.booking.domain.model.BookingStatus;
import ru.practicum.shareit.comment.domain.model.Comment;
import ru.practicum.shareit.comment.domain.repo.CommentRepo;
import ru.practicum.shareit.common.domain.exception.NotFoundException;
import ru.practicum.shareit.common.domain.exception.ValidationException;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User author;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(1L)
                .name("Author")
                .email("author@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        LocalDateTime now = LocalDateTime.now();

        Booking.builder()
                .id(1L)
                .item(item)
                .booker(author)
                .start(now.minusDays(3))
                .end(now.minusDays(1)) // Завершилось в прошлом
                .status(BookingStatus.APPROVED)
                .build();

        Booking.builder()
                .id(2L)
                .item(item)
                .booker(author)
                .start(now.plusDays(1))
                .end(now.plusDays(3)) // Начнется в будущем
                .status(BookingStatus.APPROVED)
                .build();

        Booking.builder()
                .id(3L)
                .item(item)
                .booker(author)
                .start(now.minusDays(1))
                .end(now.plusDays(1)) // Сейчас активное
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void save_UpdateExistingComment_Success() {
        comment.setId(1L); // Существующий комментарий
        when(commentRepo.update(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.save(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(commentRepo, times(1)).update(comment);
        verify(bookingService, never()).findByBooker(any(), any()); // Не вызывается для обновления
    }

    @Test
    void save_UpdateCommentText_Success() {
        Comment updatedComment = Comment.builder()
                .id(1L)
                .text("Updated text")
                .author(author)
                .item(item)
                .created(comment.getCreated())
                .build();

        when(commentRepo.update(any(Comment.class))).thenReturn(updatedComment);

        Comment result = commentService.save(updatedComment);

        assertNotNull(result);
        assertEquals("Updated text", result.getText());
        verify(commentRepo, times(1)).update(updatedComment);
        verify(bookingService, never()).findByBooker(any(), any());
    }

    @Test
    void save_NullComment_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.save(null));
        assertEquals("Некорректный item", exception.getMessage());
        verify(commentRepo, never()).create(any(Comment.class));
        verify(commentRepo, never()).update(any(Comment.class));
        verify(bookingService, never()).findByBooker(any(), any());
    }

    @Test
    void findByItemId_ValidItemId_ReturnsComments() {
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Another comment")
                .author(author)
                .item(item)
                .created(LocalDateTime.now().minusHours(1))
                .build();

        List<Comment> comments = List.of(comment, comment2);
        when(commentRepo.findByItemId(1L)).thenReturn(comments);

        List<Comment> result = commentService.findByItemId(1L);

        assertEquals(2, result.size());
        assertEquals("Great item!", result.get(0).getText());
        assertEquals("Another comment", result.get(1).getText());
        verify(commentRepo, times(1)).findByItemId(1L);
    }

    @Test
    void findByItemId_ItemWithoutComments_ReturnsEmptyList() {
        when(commentRepo.findByItemId(2L)).thenReturn(List.of());

        List<Comment> result = commentService.findByItemId(2L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepo, times(1)).findByItemId(2L);
    }

    @Test
    void findByItemId_MultipleItems_ReturnsCorrectComments() {
        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .build();

        Comment commentForItem1 = Comment.builder()
                .id(1L)
                .text("Comment for item 1")
                .item(item)
                .author(author)
                .build();

        Comment commentForItem2 = Comment.builder()
                .id(2L)
                .text("Comment for item 2")
                .item(item2)
                .author(author)
                .build();

        when(commentRepo.findByItemId(1L)).thenReturn(List.of(commentForItem1));
        when(commentRepo.findByItemId(2L)).thenReturn(List.of(commentForItem2));

        List<Comment> result1 = commentService.findByItemId(1L);
        List<Comment> result2 = commentService.findByItemId(2L);

        assertEquals(1, result1.size());
        assertEquals("Comment for item 1", result1.get(0).getText());
        assertEquals(1L, result1.get(0).getItem().getId());

        assertEquals(1, result2.size());
        assertEquals("Comment for item 2", result2.get(0).getText());
        assertEquals(2L, result2.get(0).getItem().getId());
    }

    @Test
    void findById_ExistingComment_ReturnsComment() {
        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great item!", result.getText());
        verify(commentRepo, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistentComment_ThrowsNotFoundException() {
        when(commentRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.findById(999L));
        assertEquals("Не найдено", exception.getMessage());
        verify(commentRepo, times(1)).findById(999L);
    }

    @Test
    void findById_NullId_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.findById(null));
        assertEquals("Некорректный id", exception.getMessage());
        verify(commentRepo, never()).findById(anyLong());
    }

    @Test
    void deleteById_ValidId_DeletesSuccessfully() {
        doNothing().when(commentRepo).deleteById(1L);

        commentService.deleteById(1L);

        verify(commentRepo, times(1)).deleteById(1L);
    }

    @Test
    void findByItemId_CommentsSortedByCreationDate() {
        Comment oldComment = Comment.builder()
                .id(1L)
                .text("Old comment")
                .author(author)
                .item(item)
                .created(LocalDateTime.now().minusDays(2))
                .build();

        Comment newComment = Comment.builder()
                .id(2L)
                .text("New comment")
                .author(author)
                .item(item)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        List<Comment> comments = List.of(oldComment, newComment);
        when(commentRepo.findByItemId(1L)).thenReturn(comments);

        List<Comment> result = commentService.findByItemId(1L);

        assertEquals(2, result.size());
        assertEquals("Old comment", result.get(0).getText());
        assertEquals("New comment", result.get(1).getText());
    }
}
