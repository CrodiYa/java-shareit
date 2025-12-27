package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.NotUniqueException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryInMemory implements UserRepository {

    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1L);

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public User addUser(User user) {
        if (isEmailTaken(user.getEmail())) {
            throw new NotUniqueException("Email " + user.getEmail() + " уже занят");
        }

        Long id = idCounter.getAndIncrement();
        user.setId(id);
        storage.put(id, user);

        return user;
    }

    @Override
    public Optional<User> updateUser(User newUser) {
        if (!storage.containsKey(newUser.getId())) {
            return Optional.empty();
        }

        User oldUser = storage.get(newUser.getId());
        patchUser(oldUser, newUser);

        return Optional.of(oldUser);

    }

    @Override
    public boolean deleteUser(Long userId) {
        User user = storage.remove(userId);

        return user != null;
    }

    @Override
    public boolean contains(Long userId) {
        return storage.containsKey(userId);
    }

    private boolean isEmailTaken(String email) {
        return storage.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private void patchUser(User oldUser, User newUser) {
        String name = newUser.getName();
        String email = newUser.getEmail();

        if (name != null) {
            oldUser.setName(name);
        }

        if (email != null) {
            if (isEmailTaken(email)) {
                throw new NotUniqueException("Email " + email + " уже занят");
            }
            oldUser.setEmail(email);
        }
    }
}
