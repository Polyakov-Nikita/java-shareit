package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@RequiredArgsConstructor
public class ServiceBase {
    protected final UserRepository userRepository;
    protected final ItemRepository itemRepository;
    protected final ItemRequestRepository itemRequestRepository;

    protected User findUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь", id));
    }

    protected Item findItem(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("предмет", id));
    }

    protected ItemRequest findItemRequest(long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("запрос", id));
    }
}
