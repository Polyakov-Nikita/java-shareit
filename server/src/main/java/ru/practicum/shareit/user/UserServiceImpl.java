package ru.practicum.shareit.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ServiceBase;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl extends ServiceBase implements UserService {
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, ItemRepository itemRepository, ItemRequestRepository itemRequestRepository,
                           UserMapper mapper) {
        super(userRepository, itemRepository, itemRequestRepository);
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = mapper.toUser(request);
        User result = save(user);
        return mapper.toUserResponse(result);
    }

    private User save(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedDataException("пользователь", "email", user.getEmail());
        }
    }

    @Override
    @Transactional
    public UserResponse updateUser(long id, UpdateUserRequest request) {
        User user = findUser(id);
        User userUpdate = mapper.toUser(user, request);
        User result = save(userUpdate);
        return mapper.toUserResponse(result);
    }

    @Override
    public UserResponse getUser(long id) {
        User result = findUser(id);
        return mapper.toUserResponse(result);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
