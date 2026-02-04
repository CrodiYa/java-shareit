package ru.practicum.shareit.item.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    @Test
    public void shouldMapCommentToCommentDto() {
        User author = new User();
        author.setName("Author Name");
        Item item = new Item();
        item.setId(1L);
        Comment comment = new Comment();
        comment.setId(100L);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setText("Comment text");
        LocalDateTime created = LocalDateTime.now();
        comment.setCreated(created);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getItem().getId(), commentDto.getItemId());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}