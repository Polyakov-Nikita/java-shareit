package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(long id, UpdateUserRequest request);

    UserResponse getUser(long id);

    void deleteUser(long id);
}
