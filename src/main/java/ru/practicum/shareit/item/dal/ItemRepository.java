package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item get(long id);

    Item update(long id, Item update);

    boolean isAbsentId(long id);

    List<Item> getAll(long sharerId);

    List<Item> search(String text);

    boolean isNotSharer(long userId, long itemId);
}
