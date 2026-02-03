package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.Collection;

public interface BookingService {
    Booking createBooking(Long bookerId, BookingDto bookingDto);

    Booking updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    Collection<Booking> findByBookerAndState(Long bookerId, BookingState state);

    Collection<Booking> findByOwnerAndState(Long ownerId, BookingState state);
}
