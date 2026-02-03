package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.BookingState;

import java.util.Collections;

import static ru.practicum.shareit.Util.BOOKING_PATH;
import static ru.practicum.shareit.Util.EMPTY_PATH;

@Component
public class BookingClient extends BaseClient {
    public BookingClient() {
        super(BOOKING_PATH);
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDto dto) {
        if (!dto.getStart().isBefore(dto.getEnd())) {
            return ResponseEntity.badRequest().build();
        }

        return post(EMPTY_PATH, userId, dto);
    }

    public ResponseEntity<Object> updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved={approved}",
                userId,
                Collections.singletonMap("approved", approved));
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findByBookerAndState(Long bookerId, BookingState state) {
        return get("?state={state}", bookerId, Collections.singletonMap("state", state));
    }

    public ResponseEntity<Object> findByOwnerAndState(Long ownerId, BookingState state) {
        return get("/owner?state={state}", ownerId, Collections.singletonMap("state", state));
    }
}
