package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.Collection;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable @Positive Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto addItem(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestBody @Validated(OnCreate.class) ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Validated(OnUpdate.class) ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                 @PathVariable @Positive Long itemId,
                                 @RequestBody CommentCreate commentText) {

        return itemService.addComment(userId, itemId, commentText);
    }
}
