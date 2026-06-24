package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

@Component
public class UserMapper {
    public User toUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public User toUser(User user, UpdateUserRequest request) {
        String nameUpdate = request.getName();
        if (nameUpdate != null) {
            user.setName(nameUpdate);
        }
        String emailUpdate = request.getEmail();
        if (emailUpdate != null) {
            user.setEmail(emailUpdate);
        }
        return user;
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
