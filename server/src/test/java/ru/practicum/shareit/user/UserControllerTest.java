package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.test.ControllerTest;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class UserControllerTest extends ControllerTest {
    @InjectMocks
    private UserController controller;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void createUser_StatusCreated() {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .name("User Name")
                .email("mail@example.com")
                .build();

        // Act
        ResultActions result = performPost(UserController.URL_BASE, request);

        // Assert
        expectStatusCreated(result);
        expectMethodCall(userService, service ->
                service.createUser(Mockito.any(CreateUserRequest.class)));
    }

    @Test
    public void updateUser_StatusOk() {
        // Arrange
        long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("User Name Update")
                .email("mailupdate@example.com")
                .build();

        // Act
        ResultActions result = performPatch(createIdUrl(UserController.URL_BASE, userId), request);

        // Assert
        expectStatusOk(result);
        expectMethodCall(userService, service ->
                service.updateUser(Mockito.eq(userId), Mockito.any(UpdateUserRequest.class)));
    }

    @Test
    public void getUser_StatusOk() {
        // Arrange
        long userId = 1L;

        // Act
        ResultActions result = performGet(createIdUrl(UserController.URL_BASE, userId));

        // Assert
        expectStatusOk(result);
        expectMethodCall(userService, service ->
                service.getUser(Mockito.eq(userId)));
    }

    @Test
    public void deleteUser_StatusNoContent() {
        // Arrange
        long userId = 1L;

        // Act
        ResultActions result = performDelete(createIdUrl(UserController.URL_BASE, userId));

        // Assert
        expectStatusNoContent(result);
        expectMethodCall(userService, service ->
                service.deleteUser(Mockito.eq(userId)));
    }
}
