package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUser(Long userId);

    User addUser(User user);

    Optional<User> updateUser(User user);

    boolean deleteUser(Long userId);

    boolean contains(Long userId);
}
