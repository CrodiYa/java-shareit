package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.Constants.ALL;
import static ru.practicum.shareit.Constants.USER_ID_HEADER;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {

        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBookingStatus(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved) {

        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId) {

        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<Booking> findByBookerAndState(
            @RequestHeader(USER_ID_HEADER) @Positive Long bookerId,
            @RequestParam(defaultValue = ALL) BookingState state) {

        return bookingService.findByBookerAndState(bookerId, state);
    }

    @GetMapping("/owner")
    public Collection<Booking> findByOwnerAndState(
            @RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
            @RequestParam(defaultValue = ALL) BookingState state) {

        return bookingService.findByOwnerAndState(ownerId, state);
    }
}
