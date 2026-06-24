package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(long sharerId, CreateItemRequest request);

    ItemResponse updateItem(long id, long sharerId, UpdateItemRequest request);

    GetItemResponse getItem(long id, long sharerId);

    List<GetItemResponse> getAllItems(long sharerId);

    List<ItemResponse> searchItems(String text);

    CommentResponse createComment(long sharerId, long itemId, CreateCommentRequest request);
}
