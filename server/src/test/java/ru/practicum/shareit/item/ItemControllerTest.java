package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.test.ControllerTest;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ItemControllerTest extends ControllerTest {
    @InjectMocks
    private ItemController controller;
    @Mock
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void create_StatusCreated() {
        // Arrange
        long sharerId = 1;
        CreateItemRequest request = CreateItemRequest.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();

        // Act
        ResultActions result = performPost(sharerId, ItemController.URL_BASE, request);

        // Assert
        expectStatusCreated(result);
        expectMethodCall(itemService, service ->
                service.createItem(Mockito.eq(sharerId), Mockito.any(CreateItemRequest.class)));
    }

    @Test
    public void createComment_StatusCreated() {
        // Arrange
        long sharerId = 1;
        long itemId = 1;
        CreateCommentRequest request = CreateCommentRequest.builder()
                .text("Text")
                .build();

        // Act
        ResultActions result = performPost(sharerId, createCommentPostUrl(itemId), request);

        // Assert
        expectStatusCreated(result);
        expectMethodCall(itemService, service ->
                service.createComment(Mockito.eq(sharerId), Mockito.eq(itemId), Mockito.any(CreateCommentRequest.class)));
    }

    @Test
    public void updateItem_StatusOk() {
        // Arrange
        long sharerId = 1;
        long itemId = 1;
        UpdateItemRequest request = UpdateItemRequest.builder()
                .name("Name Update")
                .description("Description Update")
                .available(false)
                .build();

        // Act
        ResultActions result = performPatch(sharerId, createIdUrl(ItemController.URL_BASE, itemId), request);

        // Assert
        expectStatusOk(result);
        expectMethodCall(itemService, service ->
                service.updateItem(Mockito.eq(itemId), Mockito.eq(sharerId), Mockito.any(UpdateItemRequest.class)));
    }

    @Test
    public void getItem_StatusOk() {
        // Arrange
        long sharerId = 1;
        long itemId = 1;

        // Act
        ResultActions result = performGet(sharerId, createIdUrl(ItemController.URL_BASE, itemId));

        // Assert
        expectStatusOk(result);
        expectMethodCall(itemService, service ->
                service.getItem(Mockito.eq(itemId), Mockito.eq(sharerId)));
    }

    @Test
    public void getAllItems_StatusOk() {
        // Arrange
        long sharerId = 1;

        // Act
        ResultActions result = performGet(sharerId, ItemController.URL_BASE);

        // Assert
        expectStatusOk(result);
        expectMethodCall(itemService, service ->
                service.getAllItems(Mockito.eq(sharerId)));
    }

    @Test
    public void searchItems_StatusOk() {
        // Arrange
        String text = "text";

        // Act
        ResultActions result = performGet(createSearchItemsUrl(text));

        // Assert
        expectStatusOk(result);
        expectMethodCall(itemService, service ->
                service.searchItems(Mockito.eq(text)));
    }

    private String createCommentPostUrl(long itemId) {
        return String.format("%s/%d%s", ItemController.URL_BASE, itemId, ItemController.URL_COMMENT);
    }

    private String createSearchItemsUrl(String text) {
        return String.format("%s/%s?%s=%s", ItemController.URL_BASE, ItemController.URL_SEARCH, ItemController.PARAM_TEXT, text);
    }
}
