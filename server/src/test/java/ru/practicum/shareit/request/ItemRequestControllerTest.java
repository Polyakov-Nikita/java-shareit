package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.request.dto.CreateItemRequestRequest;
import ru.practicum.shareit.test.ControllerTest;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ItemRequestControllerTest extends ControllerTest {
    @InjectMocks
    private ItemRequestController controller;
    @Mock
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void createItemRequest_StatusCreated() {
        // Arrange
        long sharerId = 1L;
        CreateItemRequestRequest request = CreateItemRequestRequest.builder()
                .description("Description")
                .build();

        // Act
        ResultActions result = performPost(sharerId, ItemRequestController.URL_BASE, request);

        // Assert
        expectStatusCreated(result);
        expectMethodCall(itemRequestService, service ->
                service.createItemRequest(Mockito.eq(sharerId), Mockito.any(CreateItemRequestRequest.class)));
    }

    @Test
    public void getAllUserItemRequests_StatusOk() {
        // Arrange
        long sharerId = 1L;

        // Act
        ResultActions result = performGet(sharerId, ItemRequestController.URL_BASE);

        // Assert
        expectStatusOk(result);
        expectMethodCall(itemRequestService, service -> service.getAllUserItemRequests(sharerId));
    }

    @Test
    public void getAllOtherItemRequests_StatusOk() {
        // Arrange
        long sharerId = 1L;

        // Act
        ResultActions result = performGet(sharerId, createGetAllOtherItemRequestsUrl());

        // Assert
        expectStatusOk(result);
        expectMethodCall(itemRequestService, service -> service.getAllOtherItemRequests(sharerId));
    }

    private String createGetAllOtherItemRequestsUrl() {
        return String.format("%s/%s",
                ItemRequestController.URL_BASE, ItemRequestController.URL_OTHER);
    }

    @Test
    public void getItemRequest_StatusOk() {
        // Arrange
        long requestId = 1L;

        // Act
        ResultActions result = performGet(createGetItemRequestUrl(requestId));

        // Assert
        expectStatusOk(result);
        expectMethodCall(itemRequestService, service -> service.getItemRequest(requestId));
    }

    private String createGetItemRequestUrl(long requestId) {
        return String.format("%s/%d",
                ItemRequestController.URL_BASE, requestId);
    }
}
