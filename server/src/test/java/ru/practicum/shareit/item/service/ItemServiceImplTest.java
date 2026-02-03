package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exceptions.BadRequestException;
import ru.practicum.shareit.validation.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void shouldGetItem() {
        Item item = new Item(1L, "name", "desc", true, 1L, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of());

        ItemDto result = itemService.getItem(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("name");
        verify(itemRepository).findById(1L);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetItemWithInvalidId() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Предмет с id 999 не найден");
    }

    @Test
    public void shouldSearchItems() {
        Item item = new Item(1L, "item", "description", true, 1L, null);
        when(itemRepository.searchByNameOrDescription("desc")).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.searchItems("desc");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo("item");
        verify(itemRepository).searchByNameOrDescription("desc");
    }

    @Test
    public void shouldReturnEmptyListWhenSearchTextIsBlank() {
        Collection<ItemDto> result = itemService.searchItems("   ");

        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchByNameOrDescription(any());
    }

    @Test
    public void shouldGetItems() {
        Item item = new Item(1L, "name", "desc", true, 1L, null);
        doNothing().when(userService).throwIfUserNotFound(1L);

        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findLastAndNextDatesByOwnerId(1L)).thenReturn(List.of());
        when(commentRepository.findByItemOwnerId(1L)).thenReturn(List.of());

        Collection<ItemDto> result = itemService.getItems(1L);

        assertThat(result).hasSize(1);
        verify(itemRepository).findByOwnerId(1L);
    }

    @Test
    public void shouldAddItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("desc");
        itemDto.setAvailable(true);

        Item savedItem = new Item(1L, "item", "desc", true, 1L, null);
        doNothing().when(userService).throwIfUserNotFound(1L);

        when(itemRepository.save(any())).thenReturn(savedItem);

        ItemDto result = itemService.addItem(1L, itemDto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(itemRepository).save(any());
    }

    @Test
    public void shouldUpdateItem() {
        Item currentItem = new Item(1L, "old", "old", true, 1L, null);
        ItemDto updateDto = new ItemDto();
        updateDto.setName("new");
        updateDto.setDescription("new");

        doNothing().when(userService).throwIfUserNotFound(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(currentItem));
        when(itemRepository.save(any())).thenReturn(currentItem);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        verify(itemRepository).save(any());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateNonExistentItem() {
        ItemDto updateDto = new ItemDto();
        doNothing().when(userService).throwIfUserNotFound(1L);

        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(1L, 999L, updateDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldAddComment() {
        User user = new User(1L, "name", "email@test.com");
        Item item = new Item(1L, "item", "desc", true, 2L, null);
        CommentCreate commentCreate = new CommentCreate("comment");
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        when(userService.getUser(1L)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusIsAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, commentCreate);

        assertThat(result.getText()).isEqualTo("comment");
        verify(commentRepository).save(any());
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenAddCommentWithoutBooking() {
        User user = new User(1L, "name", "email@test.com");
        Item item = new Item(1L, "item", "desc", true, 2L, null);
        CommentCreate commentCreate = new CommentCreate("comment");

        when(userService.getUser(1L)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusIsAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addComment(1L, 1L, commentCreate))
                .isInstanceOf(BadRequestException.class);
    }
}