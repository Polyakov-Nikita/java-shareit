package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.Client;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.urlbuilder.UrlBuilder;

@Service
public class ItemClient extends Client {
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ItemController.URL_BASE))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long sharerId, CreateItemRequest request) {
        return post(sharerId, request);
    }

    public ResponseEntity<Object> createComment(long sharerId, long itemId, CreateCommentRequest request) {
        return post(UrlBuilder.start()
                        .id(itemId)
                        .pathPart(ItemController.URL_COMMENT)
                        .build(),
                sharerId, request);
    }

    public ResponseEntity<Object> updateItem(long sharerId, long itemId, UpdateItemRequest request) {
        return patch(UrlBuilder.start()
                        .id(itemId)
                        .build(),
                sharerId, request);

    }

    public ResponseEntity<Object> getItem(long sharerId, long itemId) {
        return get(UrlBuilder.start()
                        .id(itemId)
                        .build(),
                sharerId);

    }

    public ResponseEntity<Object> getAllItems(long sharerId) {
        return get(sharerId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        return get(UrlBuilder.start()
                .pathPart(ItemController.URL_SEARCH)
                .param(ItemController.PARAM_TEXT, text)
                .build());
    }
}
