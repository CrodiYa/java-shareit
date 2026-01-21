package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
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
import ru.practicum.shareit.validation.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking createBooking(Long bookerId, BookingDto bookingDto) {
        throwIfDatesInvalid(bookingDto);

        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + bookerId + " не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id " + bookingDto.getItemId() + " не найден"));

        if (!item.getIsAvailable()) {
            throw new BadRequestException("Предмет не доступен");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!isOwner(booking, userId)) {
            throw new ForbiddenException("Только владелец предмета может поменять статус");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!isBooker(booking, userId) && !isOwner(booking, userId)) {
            throw new ForbiddenException("Только владелец или арендатор могут посмотреть бронирование");
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> findByBookerAndState(Long bookerId, BookingState state) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("Пользователь с id " + bookerId + " не найден");
        }

        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatusIsOrderByEndDesc(bookerId, BookingStatus.REJECTED);
            case WAITING -> bookingRepository.findByBookerIdAndStatusIsOrderByEndDesc(bookerId, BookingStatus.WAITING);
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(bookerId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(bookerId, now);
            default -> bookingRepository.findByBookerIdOrderByEndDesc(bookerId);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> findByOwnerAndState(Long ownerId, BookingState state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }

        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusIsOrderByEndDesc(ownerId, BookingStatus.REJECTED);
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusIsOrderByEndDesc(ownerId, BookingStatus.WAITING);
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(ownerId, now, now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByEndDesc(ownerId, now);
            default -> bookingRepository.findByItemOwnerIdOrderByEndDesc(ownerId);
        };
    }

    private void throwIfDatesInvalid(BookingDto bookingDto) {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new BadRequestException("Начало не может быть после конца");
        }
    }

    private boolean isBooker(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId);
    }

    private boolean isOwner(Booking booking, Long userId) {
        return booking.getItem().getOwnerId().equals(userId);
    }
}
