package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ServiceBase;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenCommentException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl extends ServiceBase implements ItemService {
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository,
                           ItemMapper itemMapper, CommentMapper commentMapper,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        super(userRepository, itemRepository);
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public ItemResponse createItem(long sharerId, CreateItemRequest request) {
        User owner = findUser(sharerId);
        Item item = itemMapper.toItem(owner, request);
        Item result = itemRepository.save(item);
        return itemMapper.toItemResponse(result);
    }

    @Override
    @Transactional
    public CommentResponse createComment(long sharerId, long itemId, CreateCommentRequest request) {
        checkBooking(sharerId, itemId);
        Item item = findItem(itemId);
        User author = findUser(sharerId);
        Comment comment = commentMapper.toComment(item, author, request);
        Comment result = commentRepository.save(comment);
        return commentMapper.toCommentResponse(result);
    }

    private void checkBooking(long bookerId, long itemId) {
        LocalDateTime now = LocalDateTime.now();
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(bookerId, itemId, now)) {
            throw new ForbiddenCommentException(bookerId, itemId);
        }
    }

    @Override
    @Transactional
    public ItemResponse updateItem(long id, long sharerId, UpdateItemRequest request) {
        Item item = findItem(id);
        checkSharerId(item, sharerId);
        Item itemUpdate = itemMapper.toItem(item, request);
        Item result = itemRepository.save(itemUpdate);
        return itemMapper.toItemResponse(result);
    }

    private void checkSharerId(Item item, long sharerId) {
        User owner = item.getOwner();
        if (owner.getId() != sharerId) {
            throw new NotOwnerException(sharerId, item.getId());
        }
    }

    @Override
    public GetItemResponse getItem(long id, long sharerId) {
        Item item = findItem(id);
        return compose(id, item);
    }

    private GetItemResponse compose(long itemId, Item item) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        LocalDate now = LocalDate.now();
        Booking lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.of(now, LocalTime.MIN));
        LocalDateTime lastBookingEnd = null;
        if (lastBooking != null) {
            lastBookingEnd = lastBooking.getEnd();
        }
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.of(now, LocalTime.MAX));
        LocalDateTime nextBookingStart = null;
        if (nextBooking != null) {
            nextBookingStart = nextBooking.getStart();
        }
        return itemMapper.toGetItemResponse(item, lastBookingEnd, nextBookingStart, comments);
    }

    @Override
    public List<GetItemResponse> getAllItems(long sharerId) {
        List<Item> result = itemRepository.findAllByOwnerId(sharerId);
        return result.stream()
                .map(item -> compose(item.getId(), item))
                .toList();
    }

    @Override
    public List<ItemResponse> searchItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        List<Item> result =
                itemRepository.findAllByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
        return result.stream()
                .map(itemMapper::toItemResponse)
                .toList();
    }
}
