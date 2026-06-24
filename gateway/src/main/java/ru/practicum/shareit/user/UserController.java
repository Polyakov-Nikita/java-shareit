package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@RestController
@RequestMapping(UserController.URL_BASE)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {
    public static final String URL_BASE = "/users";
    public static final String ID_USER = "/{userId}";

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserRequest request) {
        return userClient.createUser(request);
    }

    @PatchMapping(ID_USER)
    @Validated({UpdateUserRequest.NameUpdate.class,
            UpdateUserRequest.EmailUpdate.class})
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody UpdateUserRequest request) {
        return userClient.updateUser(userId, request);
    }

    @GetMapping(ID_USER)
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        return userClient.getUser(userId);
    }

    @DeleteMapping(ID_USER)
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        return userClient.deleteUser(userId);
    }
}
