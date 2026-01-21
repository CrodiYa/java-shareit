package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    @Override
    public User addUser(UserDto userDto) {
        return userRepository.save(UserMapper.toUser(userDto));
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User currentUser = getUser(userId);

        UserMapper.merge(currentUser, userDto);

        return userRepository.save(currentUser);
    }

    @Override
    public void deleteUser(Long userId) {
        throwIfUserNotFound(userId);
        userRepository.deleteById(userId);
    }

    public void throwIfUserNotFound(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
