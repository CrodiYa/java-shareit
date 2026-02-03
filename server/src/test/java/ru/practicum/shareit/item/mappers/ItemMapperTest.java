package ru.practicum.shareit.item.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    public void shouldMapItemDtoToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(10L);

        Item item = ItemMapper.toItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getIsAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequestId());
    }

    @Test
    public void shouldMapItemToItemDto() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Item");
        item.setDescription("Description");
        item.setIsAvailable(false);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getIsAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    public void shouldMergeItemWhenAllFieldsNonNull() {
        Item item = new Item();
        item.setName("Old name");
        item.setDescription("Old description");
        item.setIsAvailable(false);
        item.setRequestId(5L);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("New name");
        itemDto.setDescription("New description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(10L);

        Item merged = ItemMapper.merge(item, itemDto);

        assertEquals(itemDto.getName(), merged.getName());
        assertEquals(itemDto.getDescription(), merged.getDescription());
        assertEquals(itemDto.getAvailable(), merged.getIsAvailable());
        assertEquals(itemDto.getRequestId(), merged.getRequestId());
    }

    @Test
    public void shouldMergeItemWhenOnlyNameNotNull() {
        Item item = new Item();
        item.setName("Old name");
        item.setDescription("Old description");
        item.setIsAvailable(true);
        item.setRequestId(5L);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("New name");

        Item merged = ItemMapper.merge(item, itemDto);

        assertEquals(itemDto.getName(), merged.getName());
        assertEquals("Old description", merged.getDescription());
        assertTrue(merged.getIsAvailable());
        assertEquals(5L, merged.getRequestId());
    }

    @Test
    public void shouldMergeItemWhenOnlyDescriptionNotNull() {
        Item item = new Item();
        item.setName("Old name");
        item.setDescription("Old description");
        item.setIsAvailable(true);
        item.setRequestId(5L);

        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("New description");

        Item merged = ItemMapper.merge(item, itemDto);

        assertEquals("Old name", merged.getName());
        assertEquals(itemDto.getDescription(), merged.getDescription());
        assertTrue(merged.getIsAvailable());
        assertEquals(5L, merged.getRequestId());
    }

    @Test
    public void shouldMergeItemWhenOnlyAvailableNotNull() {
        Item item = new Item();
        item.setName("Old name");
        item.setDescription("Old description");
        item.setIsAvailable(true);
        item.setRequestId(5L);

        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(false);

        Item merged = ItemMapper.merge(item, itemDto);

        assertEquals("Old name", merged.getName());
        assertEquals("Old description", merged.getDescription());
        assertEquals(itemDto.getAvailable(), merged.getIsAvailable());
        assertEquals(5L, merged.getRequestId());
    }

    @Test
    public void shouldMergeItemWhenOnlyRequestIdNotNull() {
        Item item = new Item();
        item.setName("Old name");
        item.setDescription("Old description");
        item.setIsAvailable(true);
        item.setRequestId(5L);

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(15L);

        Item merged = ItemMapper.merge(item, itemDto);

        assertEquals("Old name", merged.getName());
        assertEquals("Old description", merged.getDescription());
        assertTrue(merged.getIsAvailable());
        assertEquals(itemDto.getRequestId(), merged.getRequestId());
    }

}