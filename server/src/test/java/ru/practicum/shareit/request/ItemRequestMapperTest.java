package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRequestMapperTest {

    @Test
    public void shouldMapItemRequestCreateToItemRequest() {
        ItemRequestCreate create = new ItemRequestCreate("Need a drill");
        Long requestorId = 5L;

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(create, requestorId);

        assertEquals(create.description(), itemRequest.getDescription());
        assertEquals(requestorId, itemRequest.getRequestorId());
        assertNotNull(itemRequest.getCreated());
    }

    @Test
    public void shouldMapItemRequestToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Need a saw");
        LocalDateTime created = LocalDateTime.of(2023, 5, 10, 12, 0);
        itemRequest.setCreated(created);

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
    }
}