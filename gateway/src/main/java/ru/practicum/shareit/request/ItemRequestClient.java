package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.RestClient;
import ru.practicum.shareit.request.dto.CreateItemRequestRequest;
import ru.practicum.shareit.urlbuilder.UrlBuilder;

@Service
public class ItemRequestClient extends RestClient {
    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ItemRequestController.URL_BASE))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(long sharerId, CreateItemRequestRequest request) {
        return post(sharerId, request);
    }

    public ResponseEntity<Object> getAllUserItemRequests(long sharerId) {
        return get(sharerId);
    }

    public ResponseEntity<Object> getAllOtherItemRequests(long sharerId) {
        return get(UrlBuilder.start()
                        .pathPart(ItemRequestController.URL_OTHER)
                        .build(),
                sharerId);
    }

    public ResponseEntity<Object> getItemRequest(long requestId) {
        return get(UrlBuilder.start()
                .id(requestId)
                .build());
    }
}
