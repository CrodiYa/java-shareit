package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

/**
 * TODO Sprint add-controllers.
 */
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
}
