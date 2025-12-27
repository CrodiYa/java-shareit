package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.NotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId) {
        Optional<User> maybeUser = userRepository.getUser(userId);

        return maybeUser.orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    @Override
    public User addUser(UserDto userDto) {
        return userRepository.addUser(UserMapper.toUser(userDto));
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);

        Optional<User> maybeUser = userRepository.updateUser(user);

        return maybeUser.orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    @Override
    public void deleteUser(Long userId) {
        boolean isDeleted = userRepository.deleteUser(userId);

        if (!isDeleted) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
