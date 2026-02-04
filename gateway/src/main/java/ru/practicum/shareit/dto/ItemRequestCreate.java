package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;

public record ItemRequestCreate(@NotBlank String description) {
}
