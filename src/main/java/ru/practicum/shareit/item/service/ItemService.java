package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto getItem(Long itemId);

    Collection<ItemDto> searchItems(String text);

    Collection<ItemDto> getItems(Long userId);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    CommentDto addComment(Long userId, Long itemId, CommentCreate commentText);
}