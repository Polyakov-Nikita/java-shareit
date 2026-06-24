package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.test.ServiceTest;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;

public class UserServiceImplTest extends ServiceTest {
    private final UserMapper userMapper = new UserMapper();

    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, itemRepository, null,
                userMapper);
    }

    @Test
    public void createUser_ReturnsObject() {
        // Arrange
        CreateUserRequest request = buildCreateUserRequest();
        whenSaveReturns(userRepository, this::saveUser);

        // Act
        UserResponse actual = userService.createUser(request);

        // Assert
        UserResponse expected = buildUserResponse(request);
        assertEquals(actual, expected);
    }

    @Test
    public void createUser_ExistingEmail_DuplicatedDataException() {
        // Arrange
        CreateUserRequest request = buildCreateUserRequest();
        whenSaveUserThrowsException();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> userService.createUser(request));

        // Assert
        assertDuplicatedDataException(thrown);
    }

    @Test
    public void updateUser_ReturnsObject() {
        // Arrange
        UpdateUserRequest request = buildUpdateUserRequest();
        User user = buildUser(ID);
        whenFound(user);
        whenSaveReturns(userRepository, this::saveUser);

        // Act
        UserResponse actual = userService.updateUser(ID, request);

        // Assert
        UserResponse expected = buildUserResponse(request);
        assertEquals(actual, expected);
    }

    @Test
    public void updateUser_AbsentUser_NotFoundException() {
        // Arrange
        UpdateUserRequest request = buildUpdateUserRequest();
        whenUserNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> userService.updateUser(ID, request));

        // Assert
        assertNotFoundException(thrown);
    }

    @Test
    public void updateUser_ExistingEmail_DuplicatedDataException() {
        // Arrange
        UpdateUserRequest request = buildUpdateUserRequest();
        User user = buildUser(ID);
        whenFound(user);
        whenSaveUserThrowsException();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> userService.updateUser(ID, request));

        //Assert
        assertDuplicatedDataException(thrown);
    }

    @Test
    public void getUser_ReturnsObject() {
        // Arrange
        User user = buildUser(ID);
        whenFound(user);

        // Act
        UserResponse actual = userService.getUser(ID);

        // Assert
        UserResponse expected = buildUserResponse(user);
        assertEquals(actual, expected);
    }

    @Test
    public void getUser_AbsentUser_NotFoundException() {
        // Arrange
        whenUserNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> userService.getUser(ID));

        // Assert
        assertNotFoundException(thrown);
    }

    @Test
    public void deleteUser() {
        // Arrange
        doNothingWhenDelete(ID);

        // Act
        userService.deleteUser(ID);

        // Assert
        assertMethodCall(userRepository, repository -> repository.deleteById(ID));
    }

    private void assertDuplicatedDataException(Throwable throwable) {
        Assertions.assertThat(throwable)
                .isInstanceOf(DuplicatedDataException.class);
    }

    private void whenSaveUserThrowsException() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataIntegrityViolationException(""));
    }

    private void doNothingWhenDelete(long id) {
        Mockito.doNothing()
                .when(userRepository).deleteById(Mockito.eq(id));
    }

    private CreateUserRequest buildCreateUserRequest() {
        return CreateUserRequest.builder()
                .name("Name")
                .email("email@example.com")
                .build();
    }

    private UpdateUserRequest buildUpdateUserRequest() {
        return UpdateUserRequest.builder()
                .name("New Name")
                .email("newemail@example.com")
                .build();
    }

    private UserResponse buildUserResponse(CreateUserRequest request) {
        return UserResponse.builder()
                .id(ID)
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    private UserResponse buildUserResponse(UpdateUserRequest request) {
        return UserResponse.builder()
                .id(ID)
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private User saveUser(User user) {
        return User.builder()
                .id(ID)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
