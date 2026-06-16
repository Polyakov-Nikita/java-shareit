package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.*;
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
        String nameUpdate = request.getName();
        if (nameUpdate != null) {
            item.setName(nameUpdate);
        }
        String descriptionUpdate = request.getDescription();
        if (descriptionUpdate != null) {
            item.setDescription(descriptionUpdate);
        }
        Boolean availableUpdate = request.getAvailable();
        if (availableUpdate != null) {
            item.setAvailable(availableUpdate);
        }
        return item;
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
