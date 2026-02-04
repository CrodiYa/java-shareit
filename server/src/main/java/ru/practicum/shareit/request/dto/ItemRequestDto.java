package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShort;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemShort> items;
}
