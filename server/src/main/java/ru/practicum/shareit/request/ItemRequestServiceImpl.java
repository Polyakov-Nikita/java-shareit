package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ServiceBase;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestRequest;
import ru.practicum.shareit.request.dto.GetItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl extends ServiceBase implements ItemRequestService {
    private static final Sort SORT_DESC_CREATED = Sort.by(Sort.Direction.DESC, "created");

    private final ItemRequestMapper mapper;

    public ItemRequestServiceImpl(UserRepository userRepository, ItemRepository itemRepository, ItemRequestRepository itemRequestRepository,
                                  ItemRequestMapper mapper) {
        super(userRepository, itemRepository, itemRequestRepository);
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ItemRequestResponse createItemRequest(long sharerId, CreateItemRequestRequest request) {
        User requestor = findUser(sharerId);
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = mapper.toItemRequest(requestor, created, request);
        ItemRequest result = itemRequestRepository.save(itemRequest);
        return mapper.toItemRequestResponse(result);
    }

    @Override
    public List<GetItemRequestResponse> getAllUserItemRequests(long sharerId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorId(sharerId, SORT_DESC_CREATED);
        return findRequestedItems(itemRequests);
    }

    public List<GetItemRequestResponse> findRequestedItems(List<ItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            return List.of();
        }
        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();
        Map<Long, List<Item>> itemRequestIdItems = findItems(itemRequestIds);
        return itemRequests.stream()
                .map(itemRequest -> mapper.toGetItemRequestResponse(
                        itemRequest,
                        itemRequestIdItems.getOrDefault(itemRequest.getId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<Item>> findItems(List<Long> itemRequestIds) {
        List<Item> allItems = itemRepository.findByRequestIdIn(itemRequestIds);
        return allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
    }

    @Override
    public List<GetItemRequestResponse> getAllOtherItemRequests(long sharerId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllOther(sharerId);
        return findRequestedItems(itemRequests);
    }

    @Override
    public GetItemRequestResponse getItemRequest(long requestId) {
        ItemRequest itemRequest = findItemRequest(requestId);
        List<Item> requestedItems = itemRepository.findByRequestId(itemRequest.getId());
        return mapper.toGetItemRequestResponse(itemRequest, requestedItems);
    }
}
