package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@RestController
@RequestMapping(ItemController.URL_BASE)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ItemController {
    public static final String URL_BASE = "/items";
    public static final String URL_SEARCH = "/search";
    public static final String URL_COMMENT = "/comment";
    public static final String PARAM_TEXT = "text";
    public static final String ID_ITEM = "/{itemId}";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                             @RequestBody @Valid CreateItemRequest request) {
        return itemClient.createItem(sharerId, request);
    }

    @PostMapping(ID_ITEM + URL_COMMENT)
    public ResponseEntity<Object> createComment(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid CreateCommentRequest request) {
        return itemClient.createComment(sharerId, itemId, request);
    }

    @PatchMapping(ID_ITEM)
    @Validated({UpdateItemRequest.NameUpdate.class,
            UpdateItemRequest.DescriptionUpdate.class,
            UpdateItemRequest.AvailableUpdate.class})
    public ResponseEntity<Object> updateItem(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                             @PathVariable long itemId,
                                             @RequestBody UpdateItemRequest request) {
        return itemClient.updateItem(sharerId, itemId, request);
    }

    @GetMapping(ID_ITEM)
    public ResponseEntity<Object> getItem(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId,
                                          @PathVariable long itemId) {
        return itemClient.getItem(sharerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(ShareItGateway.HEADER_SHARER) long sharerId) {
        return itemClient.getAllItems(sharerId);
    }

    @GetMapping(URL_SEARCH)
    public ResponseEntity<Object> searchItems(@RequestParam(name = PARAM_TEXT) String text) {
        return itemClient.searchItems(text);
    }
}
