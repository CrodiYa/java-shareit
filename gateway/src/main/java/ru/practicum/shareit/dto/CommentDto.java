package ru.practicum.shareit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long itemId;
    private String authorName;
    private String text;
    private LocalDateTime created;
}
