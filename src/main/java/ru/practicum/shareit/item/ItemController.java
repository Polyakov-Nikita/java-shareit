package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public static final String HEADER_SHARER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestHeader(HEADER_SHARER) long sharerId,
                                                   @RequestBody @Valid CreateItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(sharerId, request));
    }

    @PostMapping("/{itemId}" + URL_COMMENT)
    public ResponseEntity<CommentResponse> createComment(@RequestHeader(HEADER_SHARER) long sharerId,
                                                         @PathVariable long itemId,
                                                         @RequestBody @Valid CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createComment(sharerId, itemId, request));
    }

    @PatchMapping("/{itemId}")
    @Validated({UpdateItemRequest.NameUpdate.class,
            UpdateItemRequest.DescriptionUpdate.class,
            UpdateItemRequest.AvailableUpdate.class})
    public ResponseEntity<ItemResponse> updateItem(@RequestHeader(HEADER_SHARER) long sharerId,
                                                   @PathVariable long itemId,
                                                   @RequestBody UpdateItemRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(itemId, sharerId, request));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<GetItemResponse> getItem(@RequestHeader(HEADER_SHARER) long sharerId,
                                                   @PathVariable long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItem(itemId, sharerId));
    }

    @GetMapping
    public ResponseEntity<List<GetItemResponse>> getAllItems(@RequestHeader(HEADER_SHARER) long sharerId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getAllItems(sharerId));
    }

    @GetMapping(URL_SEARCH)
    public ResponseEntity<List<ItemResponse>> searchItems(@RequestParam(name = PARAM_TEXT) String text) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.searchItems(text));
    }
}
