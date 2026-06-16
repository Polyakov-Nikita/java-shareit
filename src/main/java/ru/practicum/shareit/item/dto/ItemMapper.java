package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemMapper {
    public Item toItem(User owner, CreateItemRequest request) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .owner(owner)
                .build();
    }

    public Item toItem(Item item, UpdateItemRequest request) {
        updateName(item, request.getName());
        updateDescription(item, request.getDescription());
        updateAvailable(item, request.getAvailable());
        return item;
    }

    private void updateName(Item item, String name) {
        if (name != null) {
            item.setName(name);
        }
    }

    private void updateDescription(Item item, String description) {
        if (description != null) {
            item.setDescription(description);
        }
    }

    private void updateAvailable(Item item, Boolean available) {
        if (available != null) {
            item.setAvailable(available);
        }
    }

    public ItemResponse toItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public GetItemResponse toGetItemResponse(Item item,
                                             LocalDateTime lastBooking, LocalDateTime nextBooking,
                                             List<Comment> comments) {
        return GetItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments.stream()
                        .map(this::getItemComment)
                        .toList())
                .build();
    }

    private ItemComment getItemComment(Comment comment) {
        return ItemComment.builder()
                .text(comment.getText())
                .build();
    }
}
