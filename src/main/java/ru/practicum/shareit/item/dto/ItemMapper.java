package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class ItemMapper {
    public Item toItem(User owner, CreateItemRequest request) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .itemStatus(mapStatus(request.getAvailable()))
                .owner(owner)
                .build();
    }

    public Item toItem(User owner, UpdateItemRequest request) {
        if (request.getAvailable() != null) {
            return Item.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .itemStatus(mapStatus(request.getAvailable()))
                    .owner(owner)
                    .build();
        }
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();
    }

    private Item.ItemStatus mapStatus(boolean available) {
        if (available) {
            return Item.ItemStatus.AVAILABLE;
        }
        return Item.ItemStatus.OCCUPIED;
    }

    public ItemResponse toItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getItemStatus() == Item.ItemStatus.AVAILABLE)
                .build();
    }
}
