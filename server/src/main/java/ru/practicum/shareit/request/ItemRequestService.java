package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestRequest;
import ru.practicum.shareit.request.dto.GetItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse createItemRequest(long sharerId, CreateItemRequestRequest request);

    List<GetItemRequestResponse> getAllUserItemRequests(long sharerId);

    List<GetItemRequestResponse> getAllOtherItemRequests(long sharerId);

    GetItemRequestResponse getItemRequest(long requestId);
}
