package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exceptions.NotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void shouldGetUser() {
        User user = new User(1L, "name", "email@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("name");
        verify(userRepository).findById(1L);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetUserWithInvalidId() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    public void shouldAddUser() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@test.com");

        User savedUser = new User(1L, "name", "email@test.com");
        when(userRepository.save(any())).thenReturn(savedUser);

        User result = userService.addUser(userDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("name");
        verify(userRepository).save(any());
    }

    @Test
    public void shouldUpdateUser() {
        User currentUser = new User(1L, "old", "old@test.com");
        UserDto updateDto = new UserDto();
        updateDto.setName("new");
        updateDto.setEmail("new@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any())).thenReturn(currentUser);

        User result = userService.updateUser(1L, updateDto);

        verify(userRepository).save(any());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateNonExistentUser() {
        UserDto updateDto = new UserDto();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, updateDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeleteNonExistentUser() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldThrowIfUserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.throwIfUserNotFound(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldNotThrowWhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.throwIfUserNotFound(1L);
    }
}