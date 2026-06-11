package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(long sharerId, CreateItemRequest request);

    ItemResponse updateItem(long id, long sharerId, UpdateItemRequest request);

    ItemResponse getItem(long id, long sharerId);

    List<ItemResponse> getAllItems(long sharerId);

    List<ItemResponse> searchItems(String text);
}
