package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicatedDataException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        checkCreateUserRequest(request);
        User user = mapper.toUser(request);
        User result = userRepository.save(user);
        return mapper.toUserResponse(result);
    }

    private void checkCreateUserRequest(CreateUserRequest request) {
        String requestEmail = request.getEmail();
        if (userRepository.containsEmail(requestEmail)) {
            throw new DuplicatedDataException(User.OBJECT_TYPE, "email", requestEmail);
        }
    }

    @Override
    public UserResponse updateUser(long id, UpdateUserRequest request) {
        checkUserId(id);
        checkUpdateUserRequest(request);
        User userUpdate = mapper.toUser(request);
        User result = userRepository.update(id, userUpdate);
        return mapper.toUserResponse(result);
    }

    private void checkUserId(long id) {
        if (userRepository.isAbsentId(id)) {
            throw new NotFoundException(User.OBJECT_TYPE, id);
        }
    }

    private void checkUpdateUserRequest(UpdateUserRequest request) {
        String requestEmail = request.getEmail();
        if (userRepository.containsEmail(requestEmail)) {
            throw new DuplicatedDataException(User.OBJECT_TYPE, "email", requestEmail);
        }
    }

    @Override
    public UserResponse getUser(long id) {
        checkUserId(id);
        User result = userRepository.get(id);
        return mapper.toUserResponse(result);
    }

    @Override
    public void deleteUser(long id) {
        checkUserId(id);
        userRepository.delete(id);
    }
}
