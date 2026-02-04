package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.OnUpdate;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @Size(groups = OnUpdate.class, min = 1, message = "Название предмета должно быть заполнено")
    @NotBlank(groups = OnCreate.class, message = "Название предмета должно быть заполнено")
    private String name;
    @Size(groups = OnUpdate.class, min = 1, message = "Описание предмета должно быть заполнено")
    @NotBlank(groups = OnCreate.class, message = "Описание предмета должно быть заполнено")
    private String description;
    @NotNull(groups = OnCreate.class, message = "Статус должен быть заполнен")
    private Boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private Collection<CommentDto> comments;
    @Positive
    private Long requestId;
}
