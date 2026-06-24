package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestRequest;
import ru.practicum.shareit.request.dto.GetItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.RequestedItem;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.test.ServiceTest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Stream;

public class ItemRequestServiceImplTest extends ServiceTest {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository,
                itemRequestMapper);
    }

    @Test
    public void createItemRequest_ReturnsObject() {
        // Arrange
        CreateItemRequestRequest request = buildCreateItemRequestRequest();
        User requestor = buildUser(ID);
        whenFound(requestor);
        whenSaveReturns(itemRequestRepository, this::saveItemRequest);

        // Act
        ItemRequestResponse actual = itemRequestService.createItemRequest(requestor.getId(), request);

        // Assert
        ItemRequestResponse expected = buildItemRequestResponse(request);
        assertEqualsIgnoring(actual, expected, "created");
    }

    @Test
    public void createItemRequest_AbsentRequestor_NotFoundException() {
        // Arrange
        CreateItemRequestRequest request = buildCreateItemRequestRequest();
        whenUserNotFound();

        // Act
        long absentRequestorId = ID + 1;
        Throwable thrown = Assertions.catchThrowable(() -> itemRequestService.createItemRequest(absentRequestorId, request));

        // Assert
        assertNotFoundException(thrown);
    }

    @Test
    public void getAllUserItemRequests_ReturnsArray() {
        // Arrange
        User requestor = buildUser(ID);
        ItemRequest request1 = buildItemRequest(ID, requestor, NOW.minusDays(2));
        ItemRequest request2 = buildItemRequest(ID + 1, requestor, NOW.minusDays(1));
        List<ItemRequest> requests = List.of(
                request2,
                request1
        );
        List<Long> requestIds = getIdsFrom(requests);
        Item item1ByRequest1 = buildItem(ID, request1);
        Item item2ByRequest1 = buildItem(ID + 1, request1);
        List<Item> request1Items = List.of(
                item1ByRequest1,
                item2ByRequest1
        );
        Item item1ByRequest2 = buildItem(ID + 2, request2);
        List<Item> request2Items = List.of(
                item1ByRequest2
        );
        List<Item> allRequestedItems = Stream.concat(request1Items.stream(), request2Items.stream()).toList();
        whenAllItemRequestsOf(requestor, requests);
        whenItemsOf(requestIds, allRequestedItems);

        // Act
        List<GetItemRequestResponse> actual = itemRequestService.getAllUserItemRequests(requestor.getId());

        // Assert
        List<GetItemRequestResponse> expected = List.of(
                buildGetItemRequestResponse(request2, request2Items),
                buildGetItemRequestResponse(request1, request1Items)
        );
        assertEquals(actual, expected);
    }

    @Test
    public void getAllUserItemRequests_NoRequests_ReturnsEmptyArray() {
        // Arrange
        User requestor = buildUser(ID);
        whenNoItemRequestsFoundBy(requestor);

        // Act
        List<GetItemRequestResponse> actual = itemRequestService.getAllUserItemRequests(requestor.getId());

        // Assert
        Assertions.assertThat(actual).isEmpty();
        assertMethodNotCall(itemRepository, repository -> repository.findByRequestIdIn(Mockito.any()));
    }

    @Test
    public void getAllOtherItemRequests_ReturnsArray() {
        // Arrange
        User requestor = buildUser(ID);
        User sharer = buildUser(ID + 1);
        ItemRequest request1 = buildItemRequest(ID, requestor, NOW.minusDays(2));
        ItemRequest request2 = buildItemRequest(ID + 1, requestor, NOW.minusDays(1));
        List<ItemRequest> requests = List.of(
                request2,
                request1
        );
        List<Long> requestIds = getIdsFrom(requests);
        Item item1ByRequest1 = buildItem(ID, request1);
        Item item2ByRequest1 = buildItem(ID + 1, request1);
        List<Item> request1Items = List.of(
                item1ByRequest1,
                item2ByRequest1
        );
        Item item1ByRequest2 = buildItem(ID + 2, request2);
        List<Item> request2Items = List.of(
                item1ByRequest2
        );
        List<Item> allRequestedItems = Stream.concat(request1Items.stream(), request2Items.stream()).toList();
        whenAllOtherItemRequestsOf(sharer, requests);
        whenItemsOf(requestIds, allRequestedItems);

        // Act
        List<GetItemRequestResponse> actual = itemRequestService.getAllOtherItemRequests(sharer.getId());

        // Assert
        List<GetItemRequestResponse> expected = List.of(
                buildGetItemRequestResponse(request2, request2Items),
                buildGetItemRequestResponse(request1, request1Items)
        );
        assertEquals(actual, expected);
    }

    @Test
    public void getAllOtherItemRequests_NoRequests_ReturnsEmptyArray() {
        // Arrange
        User sharer = buildUser(ID);
        whenNoOtherItemRequestsFoundBy(sharer);

        // Act
        List<GetItemRequestResponse> actual = itemRequestService.getAllOtherItemRequests(sharer.getId());

        // Assert
        Assertions.assertThat(actual).isEmpty();
        assertMethodNotCall(itemRepository, repository -> repository.findByRequestIdIn(Mockito.any()));
    }

    @Test
    public void getItemRequest_ReturnsObject() {
        // Arrange
        User requestor = buildUser(ID);
        ItemRequest request = buildItemRequest(ID, requestor, NOW.minusDays(1));
        whenFound(request);

        // Act
        GetItemRequestResponse actual = itemRequestService.getItemRequest(request.getId());

        // Assert
        GetItemRequestResponse expected = buildGetItemRequestResponse(request);
        assertEquals(actual, expected);
    }

    @Test
    public void getItemRequest_AbsentRequest_NotFoundException() {
        // Arrange
        whenItemRequestNotFound();

        // Act
        long absentRequestId = ID + 1;
        Throwable thrown = Assertions.catchThrowable(() -> itemRequestService.getItemRequest(absentRequestId));

        // Assert
        assertNotFoundException(thrown);
    }

    private void whenAllItemRequestsOf(User requestor, List<ItemRequest> requests) {
        Mockito.when(itemRequestRepository.findByRequestorId(Mockito.eq(requestor.getId()), Mockito.any(Sort.class)))
                .thenReturn(requests);
    }

    private void whenAllOtherItemRequestsOf(User requestor, List<ItemRequest> requests) {
        Mockito.when(itemRequestRepository.findAllOther(Mockito.eq(requestor.getId())))
                .thenReturn(requests);
    }

    private void whenItemsOf(List<Long> requestIds, List<Item> items) {
        Mockito.when(itemRepository.findByRequestIdIn(Mockito.eq(requestIds)))
                .thenReturn(items);
    }

    private void whenNoItemRequestsFoundBy(User requestor) {
        Mockito.when(itemRequestRepository.findByRequestorId(Mockito.eq(requestor.getId()), Mockito.any(Sort.class)))
                .thenReturn(List.of());
    }

    private void whenNoOtherItemRequestsFoundBy(User requestor) {
        Mockito.when(itemRequestRepository.findAllOther(Mockito.eq(requestor.getId())))
                .thenReturn(List.of());
    }

    private CreateItemRequestRequest buildCreateItemRequestRequest() {
        return CreateItemRequestRequest.builder()
                .description("Description")
                .build();
    }

    private ItemRequestResponse buildItemRequestResponse(CreateItemRequestRequest request) {
        return ItemRequestResponse.builder()
                .id(ID)
                .description(request.getDescription())
                .build();
    }

    private GetItemRequestResponse buildGetItemRequestResponse(ItemRequest itemRequest) {
        return GetItemRequestResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(List.of())
                .build();
    }

    private GetItemRequestResponse buildGetItemRequestResponse(ItemRequest itemRequest, List<Item> items) {
        return GetItemRequestResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(toRequestedItems(items))
                .build();
    }

    private List<RequestedItem> toRequestedItems(List<Item> items) {
        return items.stream()
                .map(item -> RequestedItem.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .ownerId(item.getOwner().getId())
                        .build())
                .toList();
    }

    private Item buildItem(long id, ItemRequest itemRequest) {
        return Item.builder()
                .id(id)
                .name("Name")
                .description("Description")
                .available(true)
                .owner(buildUser(ID))
                .request(itemRequest)
                .build();
    }

    private ItemRequest saveItemRequest(ItemRequest itemRequest) {
        return ItemRequest.builder()
                .id(ID)
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    private List<Long> getIdsFrom(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();
    }
}
