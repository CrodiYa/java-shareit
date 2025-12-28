package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.validation.NotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItem(Long itemId) {
        Optional<Item> maybeItem = itemRepository.getItem(itemId);

        if (maybeItem.isPresent()) {
            return ItemMapper.toItemDto(maybeItem.get());
        }

        throw new NotFoundException("Предмет с id " + itemId + " не найден");
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        text = text.toLowerCase(Locale.ROOT);

        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        throwIfUserNotFound(userId);

        Collection<Item> items = itemRepository.getItems(userId);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        throwIfUserNotFound(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);

        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        throwIfUserNotFound(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        item.setId(itemId);

        Optional<Item> maybeItem = itemRepository.updateItem(item);

        if (maybeItem.isPresent()) {
            return ItemMapper.toItemDto(maybeItem.get());
        }

        throw new NotFoundException("Предмет с id " + itemId + " не найден");
    }

    private void throwIfUserNotFound(Long userId) {
        if (!userRepository.contains(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
