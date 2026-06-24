package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

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

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                   @RequestBody CreateItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createItem(sharerId, request));
    }

    @PostMapping(ID_ITEM + URL_COMMENT)
    public ResponseEntity<CommentResponse> createComment(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                         @PathVariable long itemId,
                                                         @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createComment(sharerId, itemId, request));
    }

    @PatchMapping(ID_ITEM)
    public ResponseEntity<ItemResponse> updateItem(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                   @PathVariable long itemId,
                                                   @RequestBody UpdateItemRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.updateItem(itemId, sharerId, request));
    }

    @GetMapping(ID_ITEM)
    public ResponseEntity<GetItemResponse> getItem(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId,
                                                   @PathVariable long itemId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getItem(itemId, sharerId));
    }

    @GetMapping
    public ResponseEntity<List<GetItemResponse>> getAllItems(@RequestHeader(ShareItServer.HEADER_SHARER) long sharerId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getAllItems(sharerId));
    }

    @GetMapping(URL_SEARCH)
    public ResponseEntity<List<ItemResponse>> searchItems(@RequestParam(name = PARAM_TEXT) String text) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.searchItems(text));
    }
}
