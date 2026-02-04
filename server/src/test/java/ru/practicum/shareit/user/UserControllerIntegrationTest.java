package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Random;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldGetUser() throws Exception {
        User user = userRepository.save(Random.getUser());

        mockMvc.perform(get("/users/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    public void shouldAddUser() throws Exception {
        User user = Random.getUser();
        String json = objectMapper.writeValueAsString(user);

        String responseContent = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseContent);
        Long returnedId = jsonNode.get("id").asLong();

        Optional<User> savedUser = userRepository.findById(returnedId);
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getName()).isEqualTo(user.getName());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        User user = userRepository.save(Random.getUser());
        User newUser = Random.getUser();
        String json = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(patch("/users/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newUser.getName()));

        Optional<User> updatedUser = userRepository.findById(user.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo(newUser.getName());
        assertThat(updatedUser.get().getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        User user = userRepository.save(Random.getUser());

        mockMvc.perform(delete("/users/{userId}", user.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertThat(deletedUser).isEmpty();
    }
}