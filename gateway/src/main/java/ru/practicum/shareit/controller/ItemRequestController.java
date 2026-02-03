package ru.practicum.shareit.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestCreate;


import static ru.practicum.shareit.Util.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(name = USER_ID_HEADER) Long requestorId,
                                                 @RequestBody ItemRequestCreate itemRequestCreate) {
        return itemRequestClient.addItemRequest(itemRequestCreate, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(name = USER_ID_HEADER) @Positive Long userId) {
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        return itemRequestClient.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable @Positive Long requestId) {
        return itemRequestClient.getRequest(requestId);
    }
}
