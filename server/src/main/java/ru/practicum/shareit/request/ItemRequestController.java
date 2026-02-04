package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(name = USER_ID_HEADER) Long requestorId,
                                         @RequestBody ItemRequestCreate itemRequestCreate) {
        return itemRequestService.addItemRequest(itemRequestCreate, requestorId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequests(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests() {
        return itemRequestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Long requestId) {
        return itemRequestService.getRequest(requestId);
    }
}
