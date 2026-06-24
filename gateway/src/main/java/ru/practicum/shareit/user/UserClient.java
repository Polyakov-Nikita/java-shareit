package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.RestClient;
import ru.practicum.shareit.urlbuilder.UrlBuilder;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Service
public class UserClient extends RestClient {
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + UserController.URL_BASE))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(CreateUserRequest request) {
        return post(request);
    }

    public ResponseEntity<Object> updateUser(long userId, UpdateUserRequest request) {
        return patch(UrlBuilder.start()
                        .id(userId)
                        .build(),
                request);
    }

    public ResponseEntity<Object> getUser(long userId) {
        return get(UrlBuilder.start()
                        .id(userId)
                        .build());
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        return delete(UrlBuilder.start()
                .id(userId)
                .build());
    }
}
