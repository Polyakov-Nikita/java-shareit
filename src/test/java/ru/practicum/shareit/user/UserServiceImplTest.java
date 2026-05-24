package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserMapper;

@SuppressWarnings("unused")
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser_AbsentEmail_NoExceptions() {
        Assertions.assertThatCode(() -> userService.createUser(buildCreateUser()))
                .doesNotThrowAnyException();
    }

    private CreateUserRequest buildCreateUser() {
        return CreateUserRequest.builder()
                .email("email@example.com")
                .build();
    }

    @Test
    public void createUser_ExistingEmail_DuplicatedDataException() {
        Mockito.doReturn(true)
                .when(userRepository)
                .containsEmail(Mockito.any(String.class));
        Assertions.assertThatThrownBy(() -> userService.createUser(buildCreateUser()))
                .isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    public void updateUser_ExistingUser_AbsentEmail_NoExceptions() {
        Assertions.assertThatCode(() -> userService.updateUser(1, buildUpdateUser()))
                .doesNotThrowAnyException();
    }

    private UpdateUserRequest buildUpdateUser() {
        return UpdateUserRequest.builder()
                .email("email@example.com")
                .build();
    }

    @Test
    public void updateUser_AbsentUser_NotFoundException() {
        Mockito.doReturn(true)
                .when(userRepository)
                .isAbsentId(Mockito.any(long.class));
        Assertions.assertThatThrownBy(() -> userService.updateUser(1, buildUpdateUser()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateUser_ExistingEmail_DuplicatedDataException() {
        Mockito.doReturn(true)
                .when(userRepository)
                .containsEmail(Mockito.any(String.class));
        Assertions.assertThatThrownBy(() -> userService.updateUser(1, buildUpdateUser()))
                .isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    public void getUser_ExistingUser_NoExceptions() {
        Assertions.assertThatCode(() -> userService.getUser(1))
                .doesNotThrowAnyException();
    }

    @Test
    public void getUser_AbsentUser_NotFoundException() {
        Mockito.doReturn(true)
                .when(userRepository)
                .isAbsentId(Mockito.any(long.class));
        Assertions.assertThatThrownBy(() -> userService.getUser(1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void deleteUser_ExistingUser_NoExceptions() {
        Assertions.assertThatCode(() -> userService.deleteUser(1))
                .doesNotThrowAnyException();
    }

    @Test
    public void deleteUser_AbsentUser_NotFoundException() {
        Mockito.doReturn(true)
                .when(userRepository)
                .isAbsentId(Mockito.any(long.class));
        Assertions.assertThatThrownBy(() -> userService.deleteUser(1))
                .isInstanceOf(NotFoundException.class);
    }
}
