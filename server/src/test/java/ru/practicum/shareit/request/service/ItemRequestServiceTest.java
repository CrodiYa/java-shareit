package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortWithRequestId;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.validation.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Test
    public void shouldAddItemRequest() {
        ItemRequestCreate create = new ItemRequestCreate("description");
        ItemRequest savedRequest = new ItemRequest(1L, "description", 1L, LocalDateTime.now());

        when(requestRepository.save(any())).thenReturn(savedRequest);

        ItemRequestDto result = itemRequestService.addItemRequest(create, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(requestRepository).save(any());
    }

    @Test
    public void shouldGetRequests() {
        ItemRequest request = new ItemRequest(1L, "desc", 1L, LocalDateTime.now());

        when(requestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.getRequests(1L);

        assertThat(result).hasSize(1);
        verify(requestRepository).findByRequestorIdOrderByCreatedDesc(1L);
    }

    @Test
    public void shouldGetAllRequests() {
        ItemRequest request = new ItemRequest(1L, "desc", 1L, LocalDateTime.now());

        when(requestRepository.findAll(any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.getAllRequests();

        assertThat(result).hasSize(1);
        verify(requestRepository).findAll(any(Sort.class));
    }

    @Test
    public void shouldGetRequest() {
        ItemRequest request = new ItemRequest(1L, "desc", 1L, LocalDateTime.now());
        ItemShortWithRequestId itemDto = mock(ItemShortWithRequestId.class);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(itemDto));

        ItemRequestDto result = itemRequestService.getRequest(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(requestRepository).findById(1L);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetNonExistentRequest() {
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getRequest(999L))
                .isInstanceOf(NotFoundException.class);
    }
}