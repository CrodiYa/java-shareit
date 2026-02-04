package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.UserDto;

import static ru.practicum.shareit.Util.EMPTY_PATH;
import static ru.practicum.shareit.Util.USERS_PATH;

@Component
public class UserClient extends BaseClient {

    public UserClient() {
        super(USERS_PATH);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> addUser(UserDto user) {
        return post(EMPTY_PATH, user);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto user) {
        return patch("/" + userId, user);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }
}
