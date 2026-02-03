package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.CommentCreate;
import ru.practicum.shareit.dto.ItemDto;

import java.util.Collections;

import static ru.practicum.shareit.Util.EMPTY_PATH;
import static ru.practicum.shareit.Util.ITEMS_PATH;

@Component
public class ItemClient extends BaseClient {

    public ItemClient() {
        super(ITEMS_PATH);
    }

    public ResponseEntity<Object> getItem(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }

        return get("/search?text={text}", null, Collections.singletonMap("text", text));
    }

    public ResponseEntity<Object> getItems(Long userId) {
        return get(EMPTY_PATH, userId);
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDto dto) {
        return post(EMPTY_PATH, userId, dto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreate commentText) {
        return post("/" + itemId + "/comment", userId, commentText);
    }
}
