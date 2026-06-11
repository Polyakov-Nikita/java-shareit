package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    private long currentId = 0;

    @Override
    public Item save(Item item) {
        item.setId(currentId);
        items.put(currentId, item);
        currentId++;
        return item;
    }

    @Override
    public Item update(long id, Item update) {
        Item toUpdate = items.get(id);
        updateData(toUpdate, update);
        return toUpdate;
    }

    private void updateData(Item item, Item update) {
        if (update.getName() != null) {
            item.setName(update.getName());
        }
        if (update.getDescription() != null) {
            item.setDescription(update.getDescription());
        }
        item.setAvailable(update.isAvailable());
    }

    @Override
    public Item get(long id) {
        return items.get(id);
    }

    @Override
    public boolean isAbsentId(long id) {
        return !items.containsKey(id);
    }

    @Override
    public List<Item> getAll(long sharerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == sharerId)
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.isAvailable() &&
                        (item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text)))
                .toList();
    }

    @Override
    public boolean isNotSharer(long userId, long itemId) {
        if (items.containsKey(itemId)) {
            User itemOwner = items.get(itemId).getOwner();
            if (itemOwner != null) {
                return userId != itemOwner.getId();
            }
        }
        return true;
    }
}
