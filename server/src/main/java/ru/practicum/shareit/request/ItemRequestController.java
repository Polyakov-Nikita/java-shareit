package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.request.dto.CreateItemRequestRequest;
import ru.practicum.shareit.request.dto.GetItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

@RestController
@RequestMapping(path = ItemRequestController.URL_BASE)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ItemRequestController {
    public static final String URL_BASE = "/requests";
    public static final String URL_OTHER = "/all";
    public static final String ID_REQUEST = "/{requestId}";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestResponse> createItemRequest(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                                 @RequestBody CreateItemRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemRequestService.createItemRequest(sharerId, request));
    }

    @GetMapping
    public ResponseEntity<List<GetItemRequestResponse>> getAllUserItemRequests(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getAllUserItemRequests(sharerId));
    }

    @GetMapping(URL_OTHER)
    public ResponseEntity<List<GetItemRequestResponse>> getAllOtherItemRequests(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getAllOtherItemRequests(sharerId));
    }

    @GetMapping(ID_REQUEST)
    public ResponseEntity<GetItemRequestResponse> getItemRequest(@PathVariable long requestId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getItemRequest(requestId));
    }
}
