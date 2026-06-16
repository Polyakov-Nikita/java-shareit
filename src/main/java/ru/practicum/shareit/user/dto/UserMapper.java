package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserMapper {
    public User toUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public User toUser(User user, UpdateUserRequest request) {
        updateName(user, request.getName());
        updateEmail(user, request.getEmail());
        return user;
    }

    private void updateName(User user, String name) {
        if(name != null) {
            user.setName(name);
        }
    }

    private void updateEmail(User user, String email) {
        if(email != null) {
            user.setEmail(email);
        }
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
