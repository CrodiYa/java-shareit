package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public interface LastAndNextDate {
    Long getItemId();

    LocalDateTime getLastBooking();

    LocalDateTime getNextBooking();
}
