package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemShortWithRequestId;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId(Long userId);

    Collection<ItemShortWithRequestId> findByRequestId(Long requestId);

    Collection<ItemShortWithRequestId> findAllByRequestIdIn(Collection<Long> ids);

    @Query("""
            SELECT i
            FROM Item i
            WHERE i.isAvailable = true
            AND UPPER(i.name) LIKE CONCAT('%',UPPER(:text),'%') OR
            UPPER(i.description) LIKE CONCAT('%',UPPER(:text),'%')
            """)
    Collection<Item> searchByNameOrDescription(String text);
}
