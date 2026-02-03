package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingMapperTest {

    @Test
    public void shouldMapBookingDtoToBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(2023, 1, 1, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 1, 2, 10, 0));

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertNotNull(booking);
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
    }
}