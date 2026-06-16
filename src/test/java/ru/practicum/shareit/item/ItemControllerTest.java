package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.ControllerTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.item.dto.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ItemControllerTest extends ControllerTest {
    @InjectMocks
    private ItemController controller;
    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void create_StatusCreated() {
        long sharerId = 1;
        ResultActions result = performItemPost(sharerId, buildCreateItem());
        expectStatusCreated(result);
    }

    private CreateItemRequest buildCreateItem() {
        return CreateItemRequest.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();
    }

    private ResultActions performItemPost(long sharerId, CreateItemRequest body) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.post(ItemController.URL_BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(body))
                    .header(ItemController.HEADER_SHARER, sharerId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void create_ReturnsObject() {
        ItemResponse saved = buildItemResponse(1);
        Mockito.doReturn(saved)
                .when(itemService)
                .createItem(Mockito.any(long.class),
                        Mockito.any(CreateItemRequest.class));
        ResultActions result = performItemPost(1, buildCreateItem());
        expectJSONBody(result, saved);
    }

    private ItemResponse buildItemResponse(long id) {
        return ItemResponse.builder()
                .id(id)
                .name("Name")
                .description("Description")
                .build();
    }

    @Test
    public void create_AbsentSharer_StatusNotFound() {
        long absentSharerId = 1;
        Mockito.doThrow(new NotFoundException("", absentSharerId))
                .when(itemService)
                .createItem(Mockito.any(long.class),
                        Mockito.any(CreateItemRequest.class));
        ResultActions result = performItemPost(absentSharerId, buildCreateItem());
        expectStatusNotFound(result);
    }

    @Test
    public void createComment_StatusCreated() {
        long sharerId = 1;
        long itemId = 1;
        ResultActions result = performCommentPost(sharerId, itemId, buildCreateComment());
        expectStatusCreated(result);
    }

    private CreateCommentRequest buildCreateComment() {
        return CreateCommentRequest.builder()
                .text("text")
                .build();
    }

    private ResultActions performCommentPost(long sharerId, long itemId, CreateCommentRequest body) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.post(createCommentPostUrl(itemId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(body))
                    .header(ItemController.HEADER_SHARER, sharerId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createCommentPostUrl(long itemId) {
        return String.format("%s/%s%s",
                ItemController.URL_BASE, itemId, ItemController.URL_COMMENT);
    }

    @Test
    public void createComment_ReturnsObject() {
        CommentResponse saved = buildCommentResponse();
        Mockito.doReturn(saved)
                .when(itemService)
                .createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CreateCommentRequest.class));
        long sharerId = 1;
        long itemId = 1;
        ResultActions result = performCommentPost(sharerId, itemId, buildCreateComment());
        expectJSONBody(result, saved);
    }

    private CommentResponse buildCommentResponse() {
        return CommentResponse.builder()
                .id(1)
                .text("text")
                .authorName("Author Name")
                .build();
    }

    @Test
    public void update_StatusOk() {
        long id = 1;
        ResultActions result = performItemPatch(id, buildUpdateItem());
        expectStatusOk(result);
    }

    private UpdateItemRequest buildUpdateItem() {
        return UpdateItemRequest.builder()
                .name("Name Update")
                .description("Description Update")
                .available(false)
                .build();
    }

    private ResultActions performItemPatch(long id, UpdateItemRequest body) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.patch(createItemIdUrl(id))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(body))
                    .header(ItemController.HEADER_SHARER, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createItemIdUrl(long id) {
        return String.format("%s/%d", ItemController.URL_BASE, id);
    }

    @Test
    public void update_ReturnsObject() {
        long savedId = 1;
        ItemResponse updated = buildItemResponse(savedId);
        Mockito.doReturn(updated)
                .when(itemService)
                .updateItem(Mockito.any(long.class),
                        Mockito.any(long.class),
                        Mockito.any(UpdateItemRequest.class));
        ResultActions result = performItemPatch(savedId, buildUpdateItem());
        expectJSONBody(result, updated);
    }

    @Test
    public void update_AbsentId_StatusNotFound() {
        long absentId = 5;
        Mockito.doThrow(new NotFoundException("", absentId))
                .when(itemService)
                .updateItem(Mockito.any(long.class),
                        Mockito.any(long.class),
                        Mockito.any(UpdateItemRequest.class));
        ResultActions result = performItemPatch(absentId, buildUpdateItem());
        expectStatusNotFound(result);
    }

    @Test
    public void update_AbsentSharerId_StatusNotFound() {
        long absentId = 5;
        Mockito.doThrow(new NotFoundException("", absentId))
                .when(itemService)
                .updateItem(Mockito.any(long.class),
                        Mockito.any(long.class),
                        Mockito.any(UpdateItemRequest.class));
        ResultActions result = performItemPatch(absentId, buildUpdateItem());
        expectStatusNotFound(result);
    }

    @Test
    public void update_NameOnly_StatusOk() {
        long id = 1;
        ResultActions result = performItemPatch(id, buildUpdateItemName());
        expectStatusOk(result);
    }

    private UpdateItemRequest buildUpdateItemName() {
        return UpdateItemRequest.builder()
                .name("Name Update")
                .build();
    }

    @Test
    public void update_DescriptionOnly_StatusOk() {
        long id = 1;
        ResultActions result = performItemPatch(id, buildUpdateItemDescription());
        expectStatusOk(result);
    }

    private UpdateItemRequest buildUpdateItemDescription() {
        return UpdateItemRequest.builder()
                .description("Description Update")
                .build();
    }

    @Test
    public void update_AvailableOnly_StatusOk() {
        long id = 1;
        ResultActions result = performItemPatch(id, buildUpdateItemAvailable());
        expectStatusOk(result);
    }

    private UpdateItemRequest buildUpdateItemAvailable() {
        return UpdateItemRequest.builder()
                .available(false)
                .build();
    }

    @Test
    public void update_UserIsNotSharer_StatusForbidden() {
        long absentId = 5;
        Mockito.doThrow(new NotOwnerException(1, 1))
                .when(itemService)
                .updateItem(Mockito.any(long.class),
                        Mockito.any(long.class),
                        Mockito.any(UpdateItemRequest.class));
        ResultActions result = performItemPatch(absentId, buildUpdateItem());
        expectStatusForbidden(result);
    }

    @Test
    public void get_ExistingItem_StatusOk() {
        long itemId = 1;
        ResultActions result = performItemGet(itemId);
        expectStatusOk(result);
    }

    private ResultActions performItemGet(long id) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(createItemIdUrl(id))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(ItemController.HEADER_SHARER, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void get_ExistingItem_ReturnsObject() {
        long itemId = 1;
        GetItemResponse received = buildGetItemResponse(itemId);
        Mockito.doReturn(received)
                .when(itemService)
                .getItem(Mockito.any(long.class),
                        Mockito.any(long.class));
        ResultActions result = performItemGet(itemId);
        expectJSONBody(result, received);
    }

    private GetItemResponse buildGetItemResponse(long id) {
        return GetItemResponse.builder()
                .id(id)
                .name("itemToGet Name")
                .description("itemToGet Description")
                .build();
    }

    @Test
    public void getAllItems_StatusOk() {
        long sharerId = 1;
        ResultActions result = performItemGetAll(sharerId);
        expectStatusOk(result);
    }

    private ResultActions performItemGetAll(long sharerId) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(ItemController.URL_BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(ItemController.HEADER_SHARER, sharerId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllItems_ReturnsArray() {
        int itemsCount = 4;
        List<ItemResponse> expectedResponse = createItemResponseList(itemsCount);
        Mockito.doReturn(expectedResponse)
                .when(itemService)
                .getAllItems(Mockito.any(long.class));
        long sharerId = 1;
        ResultActions result = performItemGetAll(sharerId);
        expectJSONBody(result, expectedResponse);
    }

    private List<ItemResponse> createItemResponseList(int count) {
        List<ItemResponse> responseList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            responseList.add(buildItemResponse(i));
        }
        return responseList;
    }

    @Test
    public void searchItems_StatusOk() {
        String text = "text";
        ResultActions result = performItemSearchGet(text);
        expectStatusOk(result);
    }

    private ResultActions performItemSearchGet(String text) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(createItemSearchUrl(text)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createItemSearchUrl(String text) {
        return String.format("%s/%s?%s=%s",
                ItemController.URL_BASE, ItemController.URL_SEARCH, ItemController.PARAM_TEXT, text);
    }

    @Test
    public void searchItems_ReturnsArray() {
        int itemsCount = 4;
        List<ItemResponse> expectedResponse = createItemResponseList(itemsCount);
        Mockito.doReturn(expectedResponse)
                .when(itemService)
                .searchItems(Mockito.any(String.class));
        String text = "otherText";
        ResultActions result = performItemSearchGet(text);
        expectJSONBody(result, expectedResponse);
    }
}
