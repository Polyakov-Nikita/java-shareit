package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ServiceBase;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenCommentException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl extends ServiceBase implements ItemService {
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository, ItemMapper itemMapper, CommentMapper commentMapper, BookingRepository bookingRepository, CommentRepository commentRepository) {
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
        LocalDate now = LocalDate.now();
        LocalDateTime lastBookingEnd = findLastBookingEnd(id, now);
        LocalDateTime nextBookingStart = findNextBookingStart(id, now);
        List<Comment> comments = commentRepository.findByItemId(id);
        return itemMapper.toGetItemResponse(item, lastBookingEnd, nextBookingStart, comments);
    }

    private LocalDateTime findLastBookingEnd(long itemId, LocalDate localDate) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndEndBefore(
                itemId,
                LocalDateTime.of(localDate, LocalTime.MIN),
                SORT_DESC_END
        );
        if (lastBooking != null) {
            return lastBooking.getEnd();
        }
        return null;
    }

    private LocalDateTime findNextBookingStart(long itemId, LocalDate localDate) {
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfter(
                itemId,
                LocalDateTime.of(localDate, LocalTime.MAX),
                SORT_ASC_START
        );
        if (nextBooking != null) {
            return nextBooking.getStart();
        }
        return null;
    }

    @Override
    public List<GetItemResponse> getAllItems(long sharerId) {
        List<Item> items = itemRepository.findAllByOwnerId(sharerId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        LocalDate now = LocalDate.now();
        Map<Long, Booking> itemIdLastBooking = findLastBookings(itemIds, now);
        Map<Long, Booking> itemIdNextBooking = findNextBookings(itemIds, now);
        Map<Long, List<Comment>> itemIdComments = findComments(itemIds);
        return items.stream()
                .map(item -> {
                    long itemId = item.getId();
                    return compose(item,
                            itemIdLastBooking.get(itemId),
                            itemIdNextBooking.get(itemId),
                            itemIdComments.getOrDefault(itemId, List.of()));
                })
                .toList();
    }

    private Map<Long, Booking> findLastBookings(List<Long> itemIds, LocalDate localDate) {
        LocalDateTime dayStart = LocalDateTime.of(localDate, LocalTime.MIN);
        List<Booking> lastCandidates = bookingRepository.findLastBookingsForItems(itemIds, dayStart);
        return lastCandidates.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity(),
                        (booking1, booking2) -> booking1.getEnd().isAfter(booking2.getEnd()) ? booking1 : booking2));
    }

    private Map<Long, Booking> findNextBookings(List<Long> itemIds, LocalDate localDate) {
        LocalDateTime dayEnd = LocalDateTime.of(localDate, LocalTime.MAX);
        List<Booking> nextCandidates = bookingRepository.findNextBookingsForItems(itemIds, dayEnd);
        return nextCandidates.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity(),
                        (booking1, booking2) -> booking1.getStart().isBefore(booking2.getStart()) ? booking1 : booking2));
    }

    private Map<Long, List<Comment>> findComments(List<Long> itemIds) {
        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);
        return allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }

    private GetItemResponse compose(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        LocalDateTime lastBookingEnd = null;
        if (lastBooking != null) {
            lastBookingEnd = lastBooking.getEnd();
        }
        LocalDateTime nextBookingStart = null;
        if (nextBooking != null) {
            nextBookingStart = nextBooking.getStart();
        }
        return itemMapper.toGetItemResponse(item, lastBookingEnd, nextBookingStart, comments);
    }

    @Override
    public List<ItemResponse> searchItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        List<Item> result = itemRepository.findAllByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
        return result.stream().map(itemMapper::toItemResponse).toList();
    }
}
