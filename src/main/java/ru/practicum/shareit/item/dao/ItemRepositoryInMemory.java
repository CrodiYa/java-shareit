package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1L);

    @Override
    public Optional<Item> getItem(Long itemId) {
        return Optional.ofNullable(storage.get(itemId));
    }

    @Override
    public Collection<Item> searchItems(String text) {
        return storage.values()
                .stream()
                .filter(Item::getIsAvailable)
                .filter(item ->
                        item.getName().toLowerCase(Locale.ROOT).contains(text) ||
                        item.getDescription().toLowerCase(Locale.ROOT).contains(text))
                .toList();
    }

    @Override
    public Collection<Item> getItems(Long userId) {
        return storage.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .toList();
    }

    @Override
    public Item addItem(Item item) {
        Long id = idCounter.getAndIncrement();
        item.setId(id);
        storage.put(id, item);

        return item;
    }

    @Override
    public Optional<Item> updateItem(Item newItem) {
        if (!storage.containsKey(newItem.getId())) {
            return Optional.empty();
        }

        Item oldItem = storage.get(newItem.getId());
        patchItem(oldItem, newItem);

        return Optional.of(oldItem);
    }

    private void patchItem(Item oldItem, Item newItem) {
        String name = newItem.getName();
        String description = newItem.getDescription();
        Boolean isAvailable = newItem.getIsAvailable();

        if (name != null) {
            oldItem.setName(name);
        }

        if (description != null) {
            oldItem.setDescription(description);
        }

        if (isAvailable != null) {
            oldItem.setIsAvailable(isAvailable);
        }
    }
}
