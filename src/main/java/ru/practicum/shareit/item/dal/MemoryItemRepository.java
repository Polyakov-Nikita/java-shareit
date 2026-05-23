package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoryItemRepository implements ItemRepository {
    private final List<Item> items = new ArrayList<>();

    private long currentId = 0;

    @Override
    public Item save(Item item) {
        item.setId(currentId);
        items.add(item);
        currentId++;
        return item;
    }

    @Override
    public Item update(long id, Item update) {
        return items.stream()
                .filter(user -> user.getId() == id)
                .findAny()
                .map(toUpdate -> {
                    updateData(toUpdate, update);
                    return toUpdate;
                })
                .orElse(null);
    }

    private void updateData(Item item, Item update) {
        if (update.getName() != null) {
            item.setName(update.getName());
        }
        if (update.getDescription() != null) {
            item.setDescription(update.getDescription());
        }
        if (update.getItemStatus() != null) {
            item.setItemStatus(update.getItemStatus());
        }
    }

    @Override
    public Item get(long id) {
        return items.stream()
                .filter(item -> item.getId() == id)
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean isAbsentId(long id) {
        return items.stream()
                .noneMatch(item -> item.getId() == id);
    }

    @Override
    public List<Item> getAll(long sharerId) {
        return items.stream()
                .filter(item -> item.getOwner().getId() == sharerId)
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        String textNormalized = text.toLowerCase();
        return items.stream()
                .filter(item -> item.getItemStatus() == Item.ItemStatus.AVAILABLE &&
                        (item.getName().toLowerCase().contains(textNormalized) ||
                                item.getDescription().toLowerCase().contains(textNormalized)))
                .toList();
    }
}
