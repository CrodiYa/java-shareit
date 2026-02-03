package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    public void shouldMapUserDtoToUser() {
        UserDto userDto = new UserDto();
        userDto.setName("User Name");
        userDto.setEmail("user@example.com");

        User user = UserMapper.toUser(userDto);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void shouldMergeUserWhenAllFieldsNonNull() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        UserDto userDto = new UserDto();
        userDto.setName("New Name");
        userDto.setEmail("new@example.com");

        User merged = UserMapper.merge(user, userDto);

        assertEquals(userDto.getName(), merged.getName());
        assertEquals(userDto.getEmail(), merged.getEmail());
    }

    @Test
    public void shouldMergeUserWhenOnlyNameNotNull() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        UserDto userDto = new UserDto();
        userDto.setName("New Name");

        User merged = UserMapper.merge(user, userDto);

        assertEquals(userDto.getName(), merged.getName());
        assertEquals("old@example.com", merged.getEmail());
    }

    @Test
    public void shouldMergeUserWhenOnlyEmailNotNull() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        UserDto userDto = new UserDto();
        userDto.setEmail("new@example.com");

        User merged = UserMapper.merge(user, userDto);

        assertEquals("Old Name", merged.getName());
        assertEquals(userDto.getEmail(), merged.getEmail());
    }
}