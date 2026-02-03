package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.dto.ItemShortWithRequestId;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getIsAvailable());
        return itemDto;
    }

    public static Item merge(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setIsAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }

        return item;
    }

    public static ItemShort toItemShort(ItemShortWithRequestId s) {
        return new ItemShort(s.getId(), s.getName(), s.getOwnerId());
    }
}
