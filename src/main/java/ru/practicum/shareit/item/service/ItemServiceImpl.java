package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.LastAndNextDate;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exceptions.BadRequestException;
import ru.practicum.shareit.validation.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

        ItemDto itemDto = ItemMapper.toItemDto(item);
        Collection<Comment> comments = commentRepository.findByItemId(itemId);

        return enrichWithComments(itemDto, comments);
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByNameOrDescription(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getItems(Long ownerId) {
        userService.throwIfUserNotFound(ownerId);

        Collection<Item> items = itemRepository.findByOwnerId(ownerId);

        Map<Long, LastAndNextDate> datesMap = getDatesMap(ownerId);
        Map<Long, List<CommentDto>> commentsMap = getCommentsMap(ownerId);

        return enrichWithDatesAndComments(items, datesMap, commentsMap);
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userService.throwIfUserNotFound(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        userService.throwIfUserNotFound(userId);

        Item currentItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

        currentItem.setOwnerId(userId);
        ItemMapper.merge(currentItem, itemDto);

        return ItemMapper.toItemDto(itemRepository.save(currentItem));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreate commentText) {

        User user = userService.getUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusIsAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadRequestException("Пользователь никогда не брал предмет в аренду");
        }

        Comment comment = Comment.builder()
                .text(commentText.text())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    /**
     * Обогащает коллекцию ItemDto датами бронирований и комментариями.
     *
     * @param items       исходная коллекция предметов
     * @param datesMap    таблица последних и следующих бронирований по Id предмета
     * @param commentsMap таблица комментариев по Id предмета
     * @return обогащенная коллекция ItemDto
     */
    private Collection<ItemDto> enrichWithDatesAndComments(Collection<Item> items,
                                                           Map<Long, LastAndNextDate> datesMap,
                                                           Map<Long, List<CommentDto>> commentsMap) {

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);

                    LastAndNextDate dates = datesMap.get(dto.getId());
                    if (dates != null) {
                        dto.setLastBooking(dates.getLastBooking());
                        dto.setNextBooking(dates.getNextBooking());
                    }

                    dto.setComments(commentsMap.getOrDefault(dto.getId(), Collections.emptyList()));

                    return dto;
                })
                .toList();
    }

    /**
     * Обогащает ItemDto комментариями к предмету.
     *
     * @param dto DTO предмета для обогащения
     * @return обогащенный ItemDto с комментариями
     */
    private ItemDto enrichWithComments(ItemDto dto, Collection<Comment> comments) {
        Collection<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        dto.setComments(commentsDto);

        return dto;
    }

    /**
     * Создает таблицу последних и следующих бронирований для предметов владельца.
     *
     * @param ownerId ID владельца предметов
     * @return {@code Map<Long, LastAndNextDate>} где ключ - Id предмета
     */
    private Map<Long, LastAndNextDate> getDatesMap(Long ownerId) {
        List<LastAndNextDate> dates = bookingRepository.findLastAndNextDatesByOwnerId(ownerId);

        return dates.stream()
                .collect(Collectors.toMap(
                        LastAndNextDate::getItemId,
                        Function.identity()
                ));
    }

    /**
     * Создает таблицу комментариев для всех предметов владельца.
     *
     * @param ownerId ID владельца предметов
     * @return {@code Map<Long, CommentDto>} где ключ - Id предмета
     */
    private Map<Long, List<CommentDto>> getCommentsMap(Long ownerId) {
        Collection<Comment> comments = commentRepository.findByItemOwnerId(ownerId);
        return comments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(
                                CommentMapper::toCommentDto,
                                Collectors.toList()
                        )
                ));
    }
}
