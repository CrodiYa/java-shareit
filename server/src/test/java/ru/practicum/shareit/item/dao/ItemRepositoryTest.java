package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.Random;
import ru.practicum.shareit.item.dto.ItemShortWithRequestId;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    public void shouldFindByOwnerId() {
        User user = userRepository.save(Random.getUser());

        Item item1 = ItemMapper.toItem(Random.getItemDto());
        item1.setOwnerId(user.getId());
        item1 = itemRepository.save(item1);

        Item item2 = ItemMapper.toItem(Random.getItemDto());
        item2.setOwnerId(user.getId());
        item2 = itemRepository.save(item2);

        Collection<Item> items = itemRepository.findByOwnerId(user.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getId).containsExactlyInAnyOrder(item1.getId(), item2.getId());
    }

    @Test
    public void shouldFindByRequestId() {
        User user = userRepository.save(Random.getUser());

        ItemRequest ir = new ItemRequest();
        ir.setRequestorId(user.getId());
        ir.setDescription(Random.getItemRequest().description());
        ir.setCreated(LocalDateTime.now());
        ir = itemRequestRepository.save(ir);

        User user1 = userRepository.save(Random.getUser());
        Item item1 = ItemMapper.toItem(Random.getItemDto());
        item1.setOwnerId(user1.getId());
        item1.setRequestId(ir.getId());
        item1 = itemRepository.save(item1);

        Collection<ItemShortWithRequestId> items = itemRepository.findByRequestId(ir.getId());

        assertThat(items).hasSize(1);
        assertThat(items.iterator().next().getId()).isEqualTo(item1.getId());
    }

    @Test
    public void shouldFindAllByRequestIdIn() {
        User user = userRepository.save(Random.getUser());

        ItemRequest ir1 = new ItemRequest();
        ir1.setRequestorId(user.getId());
        ir1.setDescription(Random.getItemRequest().description());
        ir1.setCreated(LocalDateTime.now());
        ir1 = itemRequestRepository.save(ir1);

        ItemRequest ir2 = new ItemRequest();
        ir2.setRequestorId(user.getId());
        ir2.setDescription(Random.getItemRequest().description());
        ir2.setCreated(LocalDateTime.now());
        ir2 = itemRequestRepository.save(ir2);

        User user1 = userRepository.save(Random.getUser());
        Item item1 = ItemMapper.toItem(Random.getItemDto());
        item1.setOwnerId(user1.getId());
        item1.setRequestId(ir1.getId());
        item1 = itemRepository.save(item1);

        Item item2 = ItemMapper.toItem(Random.getItemDto());
        item2.setOwnerId(user1.getId());
        item2.setRequestId(ir2.getId());
        item2 = itemRepository.save(item2);

        Collection<ItemShortWithRequestId> items = itemRepository.findAllByRequestIdIn(List.of(ir1.getId(), ir2.getId()));

        assertThat(items).hasSize(2);
        assertThat(items).extracting(ItemShortWithRequestId::getId).containsExactlyInAnyOrder(item1.getId(), item2.getId());
    }

    @Test
    public void shouldSearchByDescription() {
        User user = userRepository.save(Random.getUser());

        Item item1 = ItemMapper.toItem(Random.getItemDto());
        item1.setOwnerId(user.getId());
        item1.setDescription("SPEC_DESCRIPTION");
        item1 = itemRepository.save(item1);

        Collection<Item> items = itemRepository.searchByNameOrDescription(item1.getDescription());

        assertThat(items).hasSize(1);
        assertThat(items.iterator().next().getId()).isEqualTo(item1.getId());
    }
}