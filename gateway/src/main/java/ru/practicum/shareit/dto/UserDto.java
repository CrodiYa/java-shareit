package ru.practicum.shareit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.OnUpdate;

@Data
public class UserDto {
    @Size(groups = OnUpdate.class, min = 1, message = "Имя пользователя должно быть заполнено")
    @NotBlank(groups = OnCreate.class, message = "Имя пользователя должно быть заполнено")
    private String name;
    @NotNull(groups = OnCreate.class, message = "Почта должна быть заполнена")
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Почта должна соответствовать формату")
    private String email;
}
