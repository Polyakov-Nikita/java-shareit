package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {
    public static final String OBJECT_TYPE = "предмет";

    private long id;
    private String name;
    private String description;

    public enum ItemStatus {
        AVAILABLE,
        OCCUPIED
    }

    private ItemStatus itemStatus;
    private User owner;
    private ItemRequest request;
}
