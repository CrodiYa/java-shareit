package ru.practicum.shareit.item.dto;

public interface ItemShortWithRequestId {
    Long getId();

    String getName();

    Long getOwnerId();

    Long getRequestId();
}
