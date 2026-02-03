package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    public void shouldGetItem() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("item");
        when(itemService.getItem(1L)).thenReturn(dto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("item"));
    }

    @Test
    public void shouldSearchItems() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        when(itemService.searchItems("text")).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void shouldGetItems() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        when(itemService.getItems(1L)).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/items")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void shouldAddItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("desc");
        itemDto.setAvailable(true);

        ItemDto savedDto = new ItemDto();
        savedDto.setId(1L);
        when(itemService.addItem(anyLong(), any())).thenReturn(savedDto);

        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("updated");

        ItemDto updatedDto = new ItemDto();
        updatedDto.setId(1L);
        updatedDto.setName("updated");
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(updatedDto);

        mockMvc.perform(patch("/items/1")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    public void shouldAddComment() throws Exception {
        CommentCreate commentCreate = new CommentCreate("comment");
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("comment");
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("comment"));
    }
}