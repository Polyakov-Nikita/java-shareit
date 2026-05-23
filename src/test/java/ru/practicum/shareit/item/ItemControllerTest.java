package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.ControllerTest;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.handler.ErrorHandler;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

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
        MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    public void create_StatusCreated() {
        long sharerId = 1;
        ResultActions result = performItemPost(sharerId, buildCreateItem("item"));
        expectStatusCreated(result);
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

    private CreateItemRequest buildCreateItem(String prefix) {
        return CreateItemRequest.builder()
                .name(prefix + " Name")
                .description(prefix + " Description")
                .available(true)
                .build();
    }

    @Test
    public void create_ReturnsObject() {
        ItemResponse saved = buildItemResponse(1, "saved");
        Mockito.doReturn(saved)
                .when(itemService)
                .createItem(Mockito.any(long.class),
                        Mockito.any(CreateItemRequest.class));
        ResultActions result = performItemPost(1, buildCreateItem("saved"));
        expectJSONBody(result, saved);
    }

    private ItemResponse buildItemResponse(long id, String prefix) {
        return ItemResponse.builder()
                .id(id)
                .name(prefix + " Name")
                .description(prefix + " Description")
                .build();
    }

    @Test
    public void create_WithoutUserHeader_StatusBadRequest() {
        ResultActions result = performPost(ItemController.URL_BASE, buildCreateItem("item"));
        expectStatusBadRequest(result);
    }

    @Test
    public void create_AbsentSharer_StatusNotFound() {
        long absentSharerId = 1;
        Mockito.doThrow(new NotFoundException(User.OBJECT_TYPE, absentSharerId))
                .when(itemService)
                .createItem(Mockito.any(long.class),
                        Mockito.any(CreateItemRequest.class));
        ResultActions result = performItemPost(absentSharerId, buildCreateItem("item"));
        expectStatusNotFound(result);
    }

    @Test
    public void update_StatusOk() {
        long id = 1;
        ResultActions result = performItemPatch(id, buildUpdateItem("itemUpdate"));
        expectStatusOk(result);
    }

    private UpdateItemRequest buildUpdateItem(String prefix) {
        return UpdateItemRequest.builder()
                .name(prefix + " Name Update")
                .description(prefix + " Description Update")
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
        ItemResponse updated = buildItemResponse(savedId, "updated");
        Mockito.doReturn(updated)
                .when(itemService)
                .updateItem(Mockito.any(long.class),
                        Mockito.any(long.class),
                        Mockito.any(UpdateItemRequest.class));
        ResultActions result = performItemPatch(savedId, buildUpdateItem("updated"));
        expectJSONBody(result, updated);
    }

    @Test
    public void update_AbsentId_StatusNotFound() {
        long absentId = 5;
        Mockito.doThrow(new NotFoundException(Item.OBJECT_TYPE, absentId))
                .when(itemService)
                .updateItem(Mockito.any(long.class),
                        Mockito.any(long.class),
                        Mockito.any(UpdateItemRequest.class));
        ResultActions result = performItemPatch(absentId, buildUpdateItem("absent"));
        expectStatusNotFound(result);
    }

    @Test
    public void update_AbsentSharerId_StatusNotFound() {
        long absentId = 5;
        Mockito.doThrow(new NotFoundException(User.OBJECT_TYPE, absentId))
                .when(itemService)
                .updateItem(Mockito.any(long.class),
                        Mockito.any(long.class),
                        Mockito.any(UpdateItemRequest.class));
        ResultActions result = performItemPatch(absentId, buildUpdateItem("absent"));
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
        ItemResponse received = buildItemResponse(itemId, "itemToGet");
        Mockito.doReturn(received)
                .when(itemService)
                .getItem(Mockito.any(long.class),
                        Mockito.any(long.class));
        ResultActions result = performItemGet(itemId);
        expectJSONBody(result, received);
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
            responseList.add(buildItemResponse(i, "item" + i));
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
