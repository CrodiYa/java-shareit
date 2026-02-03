package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exceptions.BadRequestException;
import ru.practicum.shareit.validation.exceptions.ForbiddenException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    public void shouldCreateBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        User user = new User(1L, "booker", "booker@test.com");
        Item item = new Item(1L, "item", "desc", true, 2L, null);
        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(savedBooking);

        Booking result = bookingService.createBooking(1L, bookingDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        verify(bookingRepository).save(any());
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenCreateBookingWithInvalidDates() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now.plusDays(2));
        bookingDto.setEnd(now.plusDays(1));

        assertThatThrownBy(() -> bookingService.createBooking(1L, bookingDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenCreateBookingForUnavailableItem() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        User user = new User(1L, "booker", "booker@test.com");
        Item item = new Item(1L, "item", "desc", false, 2L, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(1L, bookingDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void shouldUpdateBookingStatus() {
        User owner = new User(2L, "owner", "owner@test.com");
        Item item = new Item(1L, "item", "desc", true, 2L, null);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(new User(1L, "booker", "booker@test.com"));
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.updateBookingStatus(2L, 1L, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository).save(any());
    }

    @Test
    public void shouldThrowForbiddenExceptionWhenUpdateBookingStatusByNonOwner() {
        Item item = new Item(1L, "item", "desc", true, 2L, null);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(new User(1L, "booker", "booker@test.com"));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBookingStatus(999L, 1L, true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void shouldGetBooking() {
        User booker = new User(1L, "booker", "booker@test.com");
        Item item = new Item(1L, "item", "desc", true, 2L, null);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(1L, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).findById(1L);
    }

    @Test
    public void shouldThrowForbiddenExceptionWhenGetBookingByUnauthorizedUser() {
        User booker = new User(1L, "booker", "booker@test.com");
        Item item = new Item(1L, "item", "desc", true, 2L, null);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBooking(999L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void shouldFindByBookerAndStateAll() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByEndDesc(1L)).thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByBookerAndState(1L, BookingState.ALL);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdOrderByEndDesc(1L);
    }

    @Test
    public void shouldFindByOwnerAndStateAll() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByEndDesc(1L)).thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByOwnerAndState(1L, BookingState.ALL);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemOwnerIdOrderByEndDesc(1L);
    }

    @Test
    public void shouldFindByBookerAndStateRejected() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.REJECTED)).thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByBookerAndState(1L, BookingState.REJECTED);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.REJECTED);
    }

    @Test
    public void shouldFindByOwnerAndStateRejected() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.REJECTED)).thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByOwnerAndState(1L, BookingState.REJECTED);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemOwnerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.REJECTED);
    }

    @Test
    public void shouldFindByBookerAndStateWaiting() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByBookerAndState(1L, BookingState.WAITING);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.WAITING);
    }

    @Test
    public void shouldFindByOwnerAndStateWaiting() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByOwnerAndState(1L, BookingState.WAITING);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemOwnerIdAndStatusIsOrderByEndDesc(1L, BookingStatus.WAITING);
    }

    @Test
    public void shouldFindByBookerAndStateCurrent() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByBookerAndState(1L, BookingState.CURRENT);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void shouldFindByOwnerAndStateCurrent() {
        when(userRepository.existsById(1L)).thenReturn(true);

        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByOwnerAndState(1L, BookingState.CURRENT);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void shouldFindByBookerAndStatePast() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByBookerAndState(1L, BookingState.PAST);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class));
    }

    @Test
    public void shouldFindByOwnerAndStatePast() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByOwnerAndState(1L, BookingState.PAST);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(any(), any(LocalDateTime.class));
    }

    @Test
    public void shouldFindByBookerAndStateFuture() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(any(), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByBookerAndState(1L, BookingState.FUTURE);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStartIsAfterOrderByEndDesc(any(), any(LocalDateTime.class));
    }

    @Test
    public void shouldFindByOwnerAndStateFuture() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByEndDesc(any(), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        Collection<Booking> result = bookingService.findByOwnerAndState(1L, BookingState.FUTURE);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemOwnerIdAndStartIsAfterOrderByEndDesc(any(), any(LocalDateTime.class));
    }
}