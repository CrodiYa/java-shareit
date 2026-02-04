package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.dto.ItemShortWithRequestId;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.validation.exceptions.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto addItemRequest(ItemRequestCreate create, Long requestorId) {
        ItemRequest ir = requestRepository.save(ItemRequestMapper.toItemRequest(create, requestorId));

        return ItemRequestMapper.toItemRequestDto(ir);
    }

    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> getRequests(Long userId) {
        Collection<ItemRequest> irs = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        return enrichWithItems(irs);
    }

    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> getAllRequests() {
        Collection<ItemRequest> irs = requestRepository.findAll(Sort.by("created").descending());

        return enrichWithItems(irs);
    }

    @Transactional
    public ItemRequestDto getRequest(Long requestId) {

        ItemRequest ir = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        Collection<ItemShort> items = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemShort)
                .toList();

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(ir);
        dto.setItems(items);

        return dto;
    }

    /**
     * Обогащает коллекцию ItemRequest представлениями предметов.
     *
     * @param irs коллекция с запросами для обогащения.
     * @return коллекцию ItemRequestDto.
     */
    private Collection<ItemRequestDto> enrichWithItems(Collection<ItemRequest> irs) {
        Collection<Long> ids = irs.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemShort>> map = getNamesMap(ids);

        return irs.stream().map(itemRequest -> {
            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);
            dto.setItems(map.get(itemRequest.getId()));

            return dto;
        }).toList();
    }

    /**
     * Создает таблицу с представлениями предмета.
     *
     * @param ids список id запросов
     * @return {@code Map<Long, ItemShort>} где ключ - Id запроса
     */
    private Map<Long, List<ItemShort>> getNamesMap(Collection<Long> ids) {
        Collection<ItemShortWithRequestId> nameAndUserIds = itemRepository.findAllByRequestIdIn(ids);

        return nameAndUserIds.stream()
                .collect(Collectors.groupingBy(
                        ItemShortWithRequestId::getRequestId,
                        Collectors.mapping(
                                ItemMapper::toItemShort,
                                Collectors.toList()
                        )
                ));
    }
}
