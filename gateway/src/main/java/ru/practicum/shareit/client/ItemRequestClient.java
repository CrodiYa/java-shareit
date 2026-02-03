package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.ItemRequestCreate;

import static ru.practicum.shareit.Util.EMPTY_PATH;
import static ru.practicum.shareit.Util.REQUEST_PATH;

@Component
public class ItemRequestClient extends BaseClient {
    public ItemRequestClient() {
        super(REQUEST_PATH);
    }

    public ResponseEntity<Object> addItemRequest(ItemRequestCreate create, Long requestorId) {

        return post(EMPTY_PATH, requestorId, create);
    }

    public ResponseEntity<Object> getRequest(Long requestId) {
        return get("/" + requestId);
    }

    public ResponseEntity<Object> getRequests(Long userId) {
        return get(EMPTY_PATH, userId);
    }

    public ResponseEntity<Object> getAllRequests() {
        return get("/all");
    }

}
