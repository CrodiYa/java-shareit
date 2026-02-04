package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findByItemId(Long itemId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.item WHERE c.item.ownerId = ?1")
    Collection<Comment> findByItemOwnerId(Long ownerId);
}
