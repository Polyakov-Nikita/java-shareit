package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@RequiredArgsConstructor
public class ServiceBase {
    protected static final Sort SORT_DESC_START = Sort.by(Sort.Direction.DESC, "start");
    protected static final Sort SORT_DESC_END = Sort.by(Sort.Direction.DESC, "end");
    protected static final Sort SORT_ASC_START = Sort.by(Sort.Direction.ASC, "start");

    protected final UserRepository userRepository;
    protected final ItemRepository itemRepository;

    protected User findUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь", id));
    }

    protected Item findItem(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("предмет", id));
    }
}
