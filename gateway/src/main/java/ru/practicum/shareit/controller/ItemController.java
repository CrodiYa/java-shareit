package ru.practicum.shareit.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.OnUpdate;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.CommentCreate;
import ru.practicum.shareit.dto.ItemDto;

import static ru.practicum.shareit.Util.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable @Positive Long itemId) {
        return client.getItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return client.searchItems(text);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return client.getItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestBody @Validated(OnCreate.class) ItemDto itemDto) {
        return client.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Validated(OnUpdate.class) ItemDto itemDto) {
        return client.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                             @PathVariable @Positive Long itemId,
                                             @RequestBody CommentCreate commentText) {

        return client.addComment(userId, itemId, commentText);
    }
}
