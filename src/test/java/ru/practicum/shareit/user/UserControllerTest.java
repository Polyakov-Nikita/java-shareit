package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.ControllerTest;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class UserControllerTest extends ControllerTest {
    @InjectMocks
    private UserController controller;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void create_StatusCreated() {
        ResultActions result = performPost(UserController.URL_BASE, buildCreateUser("user"));
        expectStatusCreated(result);
    }

    private CreateUserRequest buildCreateUser(String prefix) {
        return CreateUserRequest.builder()
                .name(prefix + "name")
                .email(prefix + "mail@example.com")
                .build();
    }

    @Test
    public void create_ReturnsObject() {
        UserResponse saved = buildUserResponse(1, "saved");
        Mockito.doReturn(saved)
                .when(userService)
                .createUser(Mockito.any(CreateUserRequest.class));
        ResultActions result = performPost(UserController.URL_BASE, buildCreateUser("saved"));
        expectJSONBody(result, saved);
    }

    private UserResponse buildUserResponse(long id, String prefix) {
        return UserResponse.builder()
                .id(id)
                .name(prefix + "name")
                .email(prefix + "mail@example.com")
                .build();
    }

    @Test
    public void create_WithoutEmail_StatusBadRequest() {
        ResultActions result = performPost(UserController.URL_BASE, buildNoEmailCreateUser());
        expectStatusBadRequest(result);
    }

    private CreateUserRequest buildNoEmailCreateUser() {
        return CreateUserRequest.builder()
                .name("No email User")
                .build();
    }

    @Test
    public void create_ExistingEmail_StatusConflict() {
        Mockito.doThrow(new DuplicatedDataException("пользователь", "email", "sameEmail"))
                .when(userService)
                .createUser(Mockito.any(CreateUserRequest.class));
        ResultActions result = performPost(UserController.URL_BASE, buildCreateUser("same"));
        expectStatusConflict(result);
    }

    @Test
    public void create_InvalidEmail_StatusBadRequest() {
        ResultActions result = performPost(UserController.URL_BASE, buildInvalidEmailCreateUser());
        expectStatusBadRequest(result);
    }

    private CreateUserRequest buildInvalidEmailCreateUser() {
        return CreateUserRequest.builder()
                .name("Invalid email User")
                .email("invalid.mail")
                .build();
    }

    @Test
    public void update_StatusOk() {
        long id = 1;
        ResultActions result = performPatch(createUserIdUrl(id), buildUpdateUser("update"));
        expectStatusOk(result);
    }

    private String createUserIdUrl(long id) {
        return String.format("%s/%d", UserController.URL_BASE, id);
    }

    private UpdateUserRequest buildUpdateUser(String prefix) {
        return UpdateUserRequest.builder()
                .name(prefix + "Name Update")
                .email(prefix + "mailupdate@example.com")
                .build();
    }

    @Test
    public void update_ReturnsObject() {
        long savedId = 1;
        UserResponse updated = buildUserResponse(savedId, "updated");
        Mockito.doReturn(updated)
                .when(userService)
                .updateUser(Mockito.any(long.class),
                        Mockito.any(UpdateUserRequest.class));
        ResultActions result = performPatch(createUserIdUrl(savedId), buildUpdateUser("updated"));
        expectJSONBody(result, updated);
    }

    @Test
    public void update_AbsentId_StatusNotFound() {
        long absentId = 5;
        Mockito.doThrow(new NotFoundException("", absentId))
                .when(userService)
                .updateUser(Mockito.any(long.class),
                        Mockito.any(UpdateUserRequest.class));
        ResultActions result = performPatch(createUserIdUrl(absentId), buildUpdateUser("absent"));
        expectStatusNotFound(result);
    }

    @Test
    public void update_NameOnly_StatusOk() {
        long id = 1;
        ResultActions result = performPatch(createUserIdUrl(id), buildUpdateUserName());
        expectStatusOk(result);
    }

    private UpdateUserRequest buildUpdateUserName() {
        return UpdateUserRequest.builder()
                .name("Name Update")
                .build();
    }

    @Test
    public void update_EmailOnly_StatusOk() {
        long id = 1;
        ResultActions result = performPatch(createUserIdUrl(id), buildUpdateUserEmail());
        expectStatusOk(result);
    }

    private UpdateUserRequest buildUpdateUserEmail() {
        return UpdateUserRequest.builder()
                .email("mailupdate@example.com")
                .build();
    }

    @Test
    public void update_ExistingEmail_StatusConflict() {
        Mockito.doThrow(new DuplicatedDataException("", "email", "sameEmail"))
                .when(userService)
                .updateUser(Mockito.any(long.class),
                        Mockito.any(UpdateUserRequest.class));
        ResultActions result = performPatch(createUserIdUrl(1), buildUpdateUser("same"));
        expectStatusConflict(result);
    }

    @Test
    public void get_ExistingUser_StatusOk() {
        ResultActions result = performGet(createUserIdUrl(1));
        expectStatusOk(result);
    }

    @Test
    public void get_ExistingUser_ReturnsObject() {
        long userId = 1;
        UserResponse received = buildUserResponse(userId, "userToGet");
        Mockito.doReturn(received)
                .when(userService)
                .getUser(Mockito.any(long.class));
        ResultActions result = performGet(createUserIdUrl(userId));
        expectJSONBody(result, received);
    }

    @Test
    public void get_AbsentUser_StatusNotFound() {
        long absentId = 5;
        Mockito.doThrow(new NotFoundException("", absentId))
                .when(userService)
                .getUser(Mockito.any(long.class));
        ResultActions result = performGet(createUserIdUrl(absentId));
        expectStatusNotFound(result);
    }

    @Test
    public void delete_ExistingUser_StatusNoContent() {
        ResultActions result = performDelete(createUserIdUrl(1));
        expectStatusNoContent(result);
    }

    @Test
    public void delete_AbsentUser_StatusNotFound() {
        long absentId = 5;
        Mockito.doThrow(new NotFoundException("", absentId))
                .when(userService)
                .deleteUser(Mockito.any(long.class));
        ResultActions result = performDelete(createUserIdUrl(absentId));
        expectStatusNotFound(result);
    }
}
