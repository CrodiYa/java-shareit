package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.Random;
import ru.practicum.shareit.booking.dto.LastAndNextDate;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    public void shouldFindLastAndNextDatesByOwnerId() {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        Booking pastBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<LastAndNextDate> dates = bookingRepository.findLastAndNextDatesByOwnerId(owner.getId());

        assertThat(dates).hasSize(1);
        assertThat(dates.getFirst().getItemId()).isEqualTo(item.getId());
    }

    @Test
    public void shouldExistsByItemIdAndBookerIdAndStatusIsAndEndBefore() {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        Boolean exists = bookingRepository.existsByItemIdAndBookerIdAndStatusIsAndEndBefore(
                item.getId(),
                booker.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );

        assertThat(exists).isTrue();
    }

    @Test
    public void shouldFindByBookerIdOrderByEndDesc() {
        User booker = userRepository.save(Random.getUser());
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        Booking booking1 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        Booking booking2 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByEndDesc(booker.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.getFirst().getEnd()).isAfter(bookings.get(1).getEnd());
    }

    @Test
    public void shouldFindByBookerIdAndStatusIsOrderByEndDesc() {
        User booker = userRepository.save(Random.getUser());
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusIsOrderByEndDesc(booker.getId(), BookingStatus.WAITING);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    public void shouldFindByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc() {
        User booker = userRepository.save(Random.getUser());
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(
                booker.getId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertThat(bookings).hasSize(1);
    }

    @Test
    public void shouldFindByBookerIdAndStartIsAfterOrderByEndDesc() {
        User booker = userRepository.save(Random.getUser());
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(
                booker.getId(),
                LocalDateTime.now()
        );

        assertThat(bookings).hasSize(1);
    }

    @Test
    public void shouldFindByBookerIdAndEndIsBeforeOrderByEndDesc() {
        User booker = userRepository.save(Random.getUser());
        User owner = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(
                booker.getId(),
                LocalDateTime.now()
        );

        assertThat(bookings).hasSize(1);
    }

    @Test
    public void shouldFindByItemOwnerIdOrderByEndDesc() {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByEndDesc(owner.getId());

        assertThat(bookings).hasSize(1);
    }

    @Test
    public void shouldFindByItemOwnerIdAndStatusIsOrderByEndDesc() {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusIsOrderByEndDesc(
                owner.getId(),
                BookingStatus.WAITING
        );

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    public void shouldFindByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc() {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(
                owner.getId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertThat(bookings).hasSize(1);
    }

    @Test
    public void shouldFindByItemOwnerIdAndStartIsAfterOrderByEndDesc() {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByEndDesc(
                owner.getId(),
                LocalDateTime.now()
        );

        assertThat(bookings).hasSize(1);
    }

    @Test
    public void shouldFindByItemOwnerIdAndEndIsBeforeOrderByEndDesc() {
        User owner = userRepository.save(Random.getUser());
        User booker = userRepository.save(Random.getUser());
        Item item = itemRepository.save(Item.builder()
                .name("item")
                .description("desc")
                .isAvailable(true)
                .ownerId(owner.getId())
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(
                owner.getId(),
                LocalDateTime.now()
        );

        assertThat(bookings).hasSize(1);
    }
}