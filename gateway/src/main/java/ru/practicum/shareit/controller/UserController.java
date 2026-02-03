package ru.practicum.shareit.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.OnUpdate;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient client;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive Long userId) {
        return client.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Validated(OnCreate.class) UserDto user) {
        return client.addUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable @Positive Long userId,
            @RequestBody @Validated(OnUpdate.class) UserDto user) {
        return client.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long userId) {
        return client.deleteUser(userId);
    }
}
