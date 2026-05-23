package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dal.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemResponse createItem(long sharerId, CreateItemRequest request) {
        checkSharerId(sharerId);
        User owner = userRepository.get(sharerId);
        Item item = mapper.toItem(owner, request);
        Item result = itemRepository.save(item);
        return mapper.toItemResponse(result);
    }

    private void checkSharerId(long id) {
        if (userRepository.isAbsentId(id)) {
            throw new NotFoundException(User.OBJECT_TYPE, id);
        }
    }

    @Override
    public ItemResponse updateItem(long id, long sharerId, UpdateItemRequest request) {
        checkItemId(id);
        checkSharerId(sharerId);
        User owner = userRepository.get(sharerId);
        Item itemUpdate = mapper.toItem(owner, request);
        Item result = itemRepository.update(id, itemUpdate);
        return mapper.toItemResponse(result);
    }

    private void checkItemId(long id) {
        if (itemRepository.isAbsentId(id)) {
            throw new NotFoundException(Item.OBJECT_TYPE, id);
        }
    }

    @Override
    public ItemResponse getItem(long id, long sharerId) {
        checkItemId(id);
        Item result = itemRepository.get(id);
        return mapper.toItemResponse(result);
    }

    @Override
    public List<ItemResponse> getAllItems(long sharerId) {
        checkSharerId(sharerId);
        List<Item> result = itemRepository.getAll(sharerId);
        return result.stream()
                .map(mapper::toItemResponse)
                .toList();
    }

    @Override
    public List<ItemResponse> searchItems(String text) {
        List<Item> result = itemRepository.search(text);
        return result.stream()
                .map(mapper::toItemResponse)
                .toList();
    }
}
