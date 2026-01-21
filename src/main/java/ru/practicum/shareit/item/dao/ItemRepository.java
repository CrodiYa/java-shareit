package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId(Long userId);

    @Query("""
            SELECT i
            FROM Item i
            WHERE i.isAvailable = true
            AND (LOWER(i.name) LIKE LOWER(%:text%)
                OR LOWER(i.description) LIKE LOWER(%:text%))
            """)
    Collection<Item> searchByNameOrDescription(String text);
}
