package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    public void shouldCreateBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        when(bookingService.createBooking(anyLong(), any())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void shouldUpdateBookingStatus() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/1")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void shouldGetBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item());
        booking.setBooker(new User());
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(get("/bookings/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void shouldFindByBookerAndState() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        when(bookingService.findByBookerAndState(anyLong(), any())).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(get("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void shouldFindByOwnerAndState() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        when(bookingService.findByOwnerAndState(anyLong(), any())).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}