package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.Random;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void shouldAddItemRequest() throws Exception {
        User user = userRepository.save(Random.getUser());

        ItemRequestCreate irc = Random.getItemRequest();

        String json = objectMapper.writeValueAsString(irc);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value(irc.description()));

        Collection<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.iterator().next().getDescription()).isEqualTo(irc.description());
    }

    @Test
    public void shouldGetRequests() throws Exception {
        User user = userRepository.save(Random.getUser());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .description("desc")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(request.getId()))
                .andExpect(jsonPath("$[0].description").value("desc"));
    }

    @Test
    public void shouldGetAllRequests() throws Exception {
        User user = userRepository.save(Random.getUser());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .description("desc")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(request.getId()));
    }

    @Test
    public void shouldGetRequest() throws Exception {
        User user = userRepository.save(Random.getUser());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .description("desc")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(user.getId())
                .requestId(request.getId())
                .build());

        mockMvc.perform(get("/requests/{requestId}", request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.items[0].id").value(item.getId()));
    }
}