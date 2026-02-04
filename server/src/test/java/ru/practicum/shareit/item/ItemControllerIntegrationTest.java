package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void shouldGetItem() throws Exception {
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        mockMvc.perform(get("/items/{itemId}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value("item"));
    }

    @Test
    public void shouldSearchItems() throws Exception {
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("searchable")
                .description("description")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        mockMvc.perform(get("/items/search")
                        .param("text", "search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(item.getId()));
    }

    @Test
    public void shouldGetItems() throws Exception {
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        mockMvc.perform(get("/items")
                        .header(Constants.USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(item.getId()));
    }

    @Test
    public void shouldAddItem() throws Exception {
        User owner = userRepository.save(User.builder().name("owner3").email("owner3@email.com").build());
        ItemDto dto = Random.getItemDto();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()));

        Collection<Item> items = itemRepository.findByOwnerId(owner.getId());
        assertThat(items).hasSize(1);
        assertThat(items.iterator().next().getName()).isEqualTo(dto.getName());
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("old")
                .description("old desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());
        ItemDto dto = Random.getItemDto();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header(Constants.USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()));

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo(dto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(dto.getDescription());
    }

    @Test
    public void shouldAddComment() throws Exception {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        CommentCreate cc = Random.getComment();

        String json = objectMapper.writeValueAsString(cc);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header(Constants.USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(cc.text()));

        assertThat(commentRepository.findByItemId(item.getId())).hasSize(1);
    }
}