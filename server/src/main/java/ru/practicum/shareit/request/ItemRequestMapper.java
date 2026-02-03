package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestCreate create, Long requestorId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(create.description());
        itemRequest.setRequestorId(requestorId);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        return dto;
    }
}
