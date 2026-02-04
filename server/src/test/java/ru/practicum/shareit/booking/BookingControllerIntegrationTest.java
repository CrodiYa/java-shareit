package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void shouldCreateBooking() throws Exception {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        BookingDto dto = Random.getBookingDto(item.getId());
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("WAITING"));

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByEndDesc(booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    public void shouldUpdateBookingStatus() throws Exception {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header(Constants.USER_ID_HEADER, owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void shouldGetBooking() throws Exception {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(Constants.USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    public void shouldFindByBookerAndState() throws Exception {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        mockMvc.perform(get("/bookings")
                        .header(Constants.USER_ID_HEADER, booker.getId())
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()));
    }

    @Test
    public void shouldFindByOwnerAndState() throws Exception {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        mockMvc.perform(get("/bookings/owner")
                        .header(Constants.USER_ID_HEADER, owner.getId())
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()));
    }
}