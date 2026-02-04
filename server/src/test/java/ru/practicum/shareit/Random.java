package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class Random {
    private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final java.util.Random random = new java.util.Random();


    public static User getUser() {
        User u = new User();
        u.setName(getRandomString());
        u.setEmail(getRandomEmail());

        return u;
    }

    public static ItemRequestCreate getItemRequest() {
        return new ItemRequestCreate(getRandomString());
    }

    public static CommentCreate getComment() {
        return new CommentCreate(getRandomString());
    }

    public static ItemDto getItemDto() {
        ItemDto dto = new ItemDto();
        dto.setName(getRandomString());
        dto.setDescription(getRandomString());
        dto.setAvailable(random.nextBoolean());

        return dto;
    }

    public static BookingDto getBookingDto(Long itemId) {
        BookingDto dto = new BookingDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }

    private static String getRandomString() {
        int length = random.nextInt(10) + 1;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(letters.length());
            sb.append(letters.charAt(index));
        }
        return sb.toString();
    }

    private static String getRandomEmail() {
        return getRandomString() + "@" + getRandomString() + ".com";
    }

}
