package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

@RestController
@RequestMapping(UserController.URL_BASE)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {
    public static final String URL_BASE = "/users";
    public static final String ID_USER = "/{userId}";

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @PatchMapping(ID_USER)
    public ResponseEntity<UserResponse> updateUser(@PathVariable long userId,
                                                   @RequestBody UpdateUserRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUser(userId, request));
    }

    @GetMapping(ID_USER)
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUser(userId));
    }

    @DeleteMapping(ID_USER)
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(null);
    }
}
