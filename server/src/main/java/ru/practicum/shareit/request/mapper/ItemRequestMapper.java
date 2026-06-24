package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestRequest;
import ru.practicum.shareit.request.dto.GetItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.RequestedItem;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequest toItemRequest(User requestor, LocalDateTime created, CreateItemRequestRequest request) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requestor(requestor)
                .created(created)
                .build();
    }

    public ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest) {
        return ItemRequestResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public GetItemRequestResponse toGetItemRequestResponse(ItemRequest itemRequest, List<Item> items) {
        return GetItemRequestResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items.stream()
                        .map(this::getRequestedItem)
                        .toList())
                .build();
    }

    private RequestedItem getRequestedItem(Item item) {
        return RequestedItem.builder()
                .itemId(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
