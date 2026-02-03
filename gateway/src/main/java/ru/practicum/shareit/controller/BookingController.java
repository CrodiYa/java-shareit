package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.BookingState;

import static ru.practicum.shareit.Util.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                @Valid @RequestBody BookingDto bookingDto) {

        return client.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved) {

        return client.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId) {

        return client.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBookerAndState(
            @RequestHeader(USER_ID_HEADER) @Positive Long bookerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {

        return client.findByBookerAndState(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerAndState(
            @RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {

        return client.findByOwnerAndState(ownerId, state);
    }
}
