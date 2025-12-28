package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItem(Long itemId);

    Collection<Item> searchItems(String text);

    Collection<Item> getItems(Long userId);

    Item addItem(Item item);

    Optional<Item> updateItem(Item item);
}
