package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.CreateItemRequestRequest;

@RestController
@RequestMapping(path = ItemRequestController.URL_BASE)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ItemRequestController {
    public static final String URL_BASE = "/requests";
    public static final String URL_OTHER = "/all";
    public static final String ID_REQUEST = "/{requestId}";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                                    @RequestBody @Valid CreateItemRequestRequest request) {
        return itemRequestClient.createItemRequest(sharerId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItemRequests(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId) {
        return itemRequestClient.getAllUserItemRequests(sharerId);
    }

    @GetMapping(URL_OTHER)
    public ResponseEntity<Object> getAllOtherItemRequests(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId) {
        return itemRequestClient.getAllOtherItemRequests(sharerId);
    }

    @GetMapping(ID_REQUEST)
    public ResponseEntity<Object> getItemRequest(@PathVariable long requestId) {
        return itemRequestClient.getItemRequest(requestId);
    }
}
