package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    public void shouldAddItemRequest() throws Exception {
        ItemRequestCreate create = new ItemRequestCreate("description");
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("description");

        when(itemRequestService.addItemRequest(any(), anyLong())).thenReturn(dto);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    public void shouldGetRequests() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        when(itemRequestService.getRequests(1L)).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void shouldGetAllRequests() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        when(itemRequestService.getAllRequests()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void shouldGetRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        when(itemRequestService.getRequest(1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}