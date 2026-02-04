package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.Random;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentRepositoryTest {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    public void shouldFindByItemId() {
        User user = Random.getUser();
        user = userRepository.save(user);

        Item item = ItemMapper.toItem(Random.getItemDto());
        item.setOwnerId(user.getId());

        item = itemRepository.save(item);

        User author = Random.getUser();
        author = userRepository.save(author);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(Random.getComment().text());
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);

        Collection<Comment> comments = commentRepository.findByItemId(item.getId());

        assertThat(comments).hasSize(1);

        Comment c = comments.iterator().next();

        assertThat(c.getId()).isEqualTo(comment.getId());
        assertThat(c.getAuthor()).isEqualTo(author);
        assertThat(c.getItem()).isEqualTo(item);
    }

    @Test
    public void shouldFindByItemOwnerId() {
        User user = Random.getUser();
        user = userRepository.save(user);

        Item item = ItemMapper.toItem(Random.getItemDto());
        item.setOwnerId(user.getId());

        item = itemRepository.save(item);

        User author = Random.getUser();
        author = userRepository.save(author);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(Random.getComment().text());
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);

        Collection<Comment> comments = commentRepository.findByItemOwnerId(item.getOwnerId());

        assertThat(comments).hasSize(1);

        Comment c = comments.iterator().next();

        assertThat(c.getId()).isEqualTo(comment.getId());
        assertThat(c.getAuthor()).isEqualTo(author);
        assertThat(c.getItem()).isEqualTo(item);
    }
}