package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.ServiceTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenCommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ItemServiceImplTest extends ServiceTest {
    private final ItemMapper itemMapper = new ItemMapper();
    private final CommentMapper commentMapper = new CommentMapper();

    private ItemServiceImpl itemService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(userRepository, itemRepository,
                itemMapper, commentMapper,
                bookingRepository, commentRepository);
    }

    @Test
    public void createItem_ReturnsObject() {
        // Arrange
        CreateItemRequest request = buildCreateItemRequest();
        User owner = buildUser(ID);
        whenFound(owner);
        whenSaveReturns(itemRepository, this::saveItem);

        // Act
        ItemResponse actual = itemService.createItem(ID, request);

        // Assert
        ItemResponse expected = buildItemResponse(request);
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void createItem_AbsentOwner_NotFoundException() {
        // Arrange
        CreateItemRequest request = buildCreateItemRequest();
        whenUserNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> itemService.createItem(ID, request));

        // Assert
        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void createComment_ReturnsObject() {
        // Arrange
        CreateCommentRequest request = buildCreateCommentRequest();
        Item item = buildItem();
        User author = buildUser(ID);
        whenBookingExists();
        whenFound(item);
        whenFound(author);
        whenSaveReturns(commentRepository, this::saveComment);

        // Act
        CommentResponse actual = itemService.createComment(ID, ID, request);

        //Assert
        CommentResponse expected = buildCommentResponse(request, author);
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("created")
                .isEqualTo(expected);
        Assertions.assertThat(actual.getCreated()).isNotNull();
    }

    @Test
    public void createComment_NoPastBooking_ForbiddenCommentException() {
        // Arrange
        CreateCommentRequest request = buildCreateCommentRequest();
        whenNoBookingExists();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> itemService.createComment(ID, ID, request));

        //Assert
        Assertions.assertThat(thrown)
                .isInstanceOf(ForbiddenCommentException.class);
    }

    @Test
    public void createComment_AbsentItem_NotFoundException() {
        // Arrange
        CreateCommentRequest request = buildCreateCommentRequest();
        whenBookingExists();
        whenItemNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> itemService.createComment(ID, ID, request));

        //Assert
        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void createComment_AbsentAuthor_NotFoundException() {
        // Arrange
        CreateCommentRequest request = buildCreateCommentRequest();
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        whenBookingExists();
        whenFound(item);
        whenUserNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> itemService.createComment(ID, ID, request));

        //Assert
        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateItem_ReturnsObject() {
        // Arrange
        UpdateItemRequest request = buildUpdateItemRequest();
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        whenFound(item);
        whenSaveReturns(itemRepository, this::saveItem);

        // Act
        ItemResponse actual = itemService.updateItem(ID, ID, request);

        // Assert
        ItemResponse expected = buildItemResponse(request);
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void updateItem_AbsentItem_NotFoundException() {
        // Arrange
        UpdateItemRequest request = buildUpdateItemRequest();
        whenItemNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> itemService.updateItem(ID, ID, request));

        //Assert
        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateItem_UserIsNotOwner_NotOwnerException() {
        // Arrange
        UpdateItemRequest request = buildUpdateItemRequest();
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        whenFound(item);

        // Act
        long otherId = ID + 1;
        Throwable thrown = Assertions.catchThrowable(() -> itemService.updateItem(ID, otherId, request));

        //Assert
        Assertions.assertThat(thrown)
                .isInstanceOf(NotOwnerException.class);
    }

    @Test
    public void getItem_ReturnsObject() {
        // Arrange
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        List<Comment> comments = List.of(buildComment(item, owner));
        Booking lastBooking = buildBooking(item, owner, NOW.minusDays(2), NOW.minusDays(1));
        Booking nextBooking = buildBooking(item, owner, NOW.plusDays(1), NOW.plusDays(2));
        whenFound(item);
        whenCommentsOf(item, comments);
        whenLastBookingOf(item, lastBooking);
        whenNextBookingOf(item, nextBooking);

        // Act
        GetItemResponse actual = itemService.getItem(ID, ID);

        // Assert
        GetItemResponse expected = buildGetItemResponse(item, lastBooking, nextBooking, comments);
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);

    }

    @Test
    public void getItem_AbsentItem_NotFoundException() {
        // Arrange
        whenItemNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> itemService.getItem(ID, ID));

        //Assert
        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getAllItems_ReturnsArray() {
        // Arrange
        User owner = buildUser(ID);
        long itemId1 = ID + 1;
        Item item1 = buildItem(itemId1, owner);
        long itemId2 = ID + 2;
        Item item2 = buildItem(itemId2, owner);
        List<Item> items = List.of(item1, item2);
        List<Comment> comments1 = List.of(buildComment(ID, item1, owner));
        List<Comment> comments2 = List.of(buildComment(ID + 1, item2, owner));
        Booking lastBooking1 = buildBooking(item1, owner, NOW.minusDays(2), NOW.minusDays(1));
        Booking nextBooking1 = buildBooking(item1, owner, NOW.plusDays(1), NOW.plusDays(2));
        Booking lastBooking2 = buildBooking(item2, owner, NOW.minusDays(3), NOW.minusDays(4));
        Booking nextBooking2 = buildBooking(item2, owner, NOW.plusDays(3), NOW.plusDays(4));
        whenItemsFoundBy(owner, items);
        List<Long> itemIds = List.of(itemId1, itemId2);
        whenLastBookingsOf(itemIds, List.of(lastBooking1, lastBooking2));
        whenNextBookingsOf(itemIds, List.of(nextBooking1, nextBooking2));
        List<Comment> allComments = Stream.concat(comments1.stream(), comments2.stream()).toList();
        whenCommentsOf(itemIds, allComments);

        // Act
        Comparator<GetItemResponse> idComparator = Comparator.comparing(GetItemResponse::getId);
        List<GetItemResponse> actual = itemService.getAllItems(owner.getId()).stream()
                .sorted(idComparator)
                .toList();

        // Assert
        List<GetItemResponse> expected = Stream.of(
                        buildGetItemResponse(item1, lastBooking1, nextBooking1, comments1),
                        buildGetItemResponse(item2, lastBooking2, nextBooking2, comments2)
                )
                .sorted(idComparator)
                .toList();
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void getAllItems_NoItems_ReturnsEmptyArray() {
        // Arrange
        User owner = buildUser(ID);
        whenNoItemsFoundBy(owner);

        // Act
        List<GetItemResponse> actual = itemService.getAllItems(owner.getId());

        // Assert
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    public void searchItems_ReturnsArray() {
        // Arrange
        User owner = buildUser(ID);
        Item item1 = buildItem(ID, owner);
        Item item2 = buildItem(ID + 1, owner);
        List<Item> items = List.of(item1, item2);
        String text = "text";
        whenSearchReturns(text, items);

        // Act
        Comparator<ItemResponse> idComparator = Comparator.comparing(ItemResponse::getId);
        List<ItemResponse> actual = itemService.searchItems(text).stream()
                .sorted(idComparator)
                .toList();

        // Assert
        List<ItemResponse> expected = Stream.of(
                        itemMapper.toItemResponse(item1),
                        itemMapper.toItemResponse(item2)
                )
                .sorted(idComparator)
                .toList();
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void searchItems_EmptyText_ReturnsEmptyArray() {
        // Arrange
        String text = "";

        // Act
        List<ItemResponse> actual = itemService.searchItems(text);

        // Assert
        Assertions.assertThat(actual).isEmpty();
        Mockito.verify(itemRepository, Mockito.never())
                .findAllByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Mockito.eq(text), Mockito.eq(text));
    }

    private void whenCommentsOf(Item item, List<Comment> comments) {
        Mockito.when(commentRepository.findByItemId(Mockito.eq(item.getId())))
                .thenReturn(comments);
    }

    private void whenLastBookingOf(Item item, Booking booking) {
        Mockito.when(bookingRepository.findFirstByItemIdAndEndBefore(
                        Mockito.eq(item.getId()),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Sort.class)))
                .thenReturn(booking);
    }

    private void whenNextBookingOf(Item item, Booking booking) {
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfter(
                        Mockito.eq(item.getId()),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Sort.class)))
                .thenReturn(booking);
    }

    private void whenLastBookingsOf(List<Long> itemIds, List<Booking> bookings) {
        Mockito.when(bookingRepository.findLastBookingsForItems(
                        Mockito.eq(itemIds),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(bookings);
    }

    private void whenNextBookingsOf(List<Long> itemIds, List<Booking> bookings) {
        Mockito.when(bookingRepository.findNextBookingsForItems(
                        Mockito.eq(itemIds),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(bookings);
    }

    private void whenCommentsOf(List<Long> itemIds, List<Comment> comments) {
        Mockito.when(commentRepository.findByItemIdIn(Mockito.eq(itemIds)))
                .thenReturn(comments);
    }

    private void whenBookingExists() {
        Mockito.when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(true);
    }

    private void whenNoBookingExists() {
        Mockito.when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(false);
    }

    private void whenItemsFoundBy(User owner, List<Item> items) {
        Mockito.when(itemRepository.findAllByOwnerId(Mockito.eq(owner.getId())))
                .thenReturn(items);
    }

    private void whenNoItemsFoundBy(User owner) {
        Mockito.when(itemRepository.findAllByOwnerId(Mockito.eq(owner.getId())))
                .thenReturn(List.of());
    }

    private void whenSearchReturns(String text, List<Item> items) {
        Mockito.when(itemRepository.findAllByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        Mockito.eq(text),
                        Mockito.eq(text))
                )
                .thenReturn(items);
    }

    private CreateItemRequest buildCreateItemRequest() {
        return CreateItemRequest.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    private CreateCommentRequest buildCreateCommentRequest() {
        return CreateCommentRequest.builder()
                .text("text")
                .build();
    }

    private UpdateItemRequest buildUpdateItemRequest() {
        return UpdateItemRequest.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    private ItemResponse buildItemResponse(CreateItemRequest request) {
        return ItemResponse.builder()
                .id(ID)
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .build();
    }

    private ItemResponse buildItemResponse(UpdateItemRequest request) {
        return ItemResponse.builder()
                .id(ID)
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .build();
    }

    private GetItemResponse buildGetItemResponse(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        return GetItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .lastBooking(lastBooking != null ? lastBooking.getEnd() : null)
                .nextBooking(nextBooking != null ? nextBooking.getStart() : null)
                .comments(comments.stream()
                        .map(this::getItemComment)
                        .toList())
                .build();
    }

    private CommentResponse buildCommentResponse(CreateCommentRequest request, User author) {
        return CommentResponse.builder()
                .id(ID)
                .text(request.getText())
                .authorName(author.getName())
                .build();
    }

    private Item buildItem() {
        return Item.builder()
                .id(ID)
                .build();
    }

    private Booking buildBooking(Item item, User booker, LocalDateTime start, LocalDateTime end) {
        return Booking.builder()
                .id(ID)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
    }

    private Comment buildComment(Item item, User author) {
        return Comment.builder()
                .id(ID)
                .text("text")
                .item(item)
                .author(author)
                .created(NOW)
                .build();
    }

    private Comment buildComment(long id, Item item, User author) {
        return Comment.builder()
                .id(id)
                .text("text")
                .item(item)
                .author(author)
                .created(NOW)
                .build();
    }

    private Item saveItem(Item item) {
        return Item.builder()
                .id(ID)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(item.getOwner())
                .build();
    }

    private Comment saveComment(Comment comment) {
        return Comment.builder()
                .id(ID)
                .text(comment.getText())
                .item(comment.getItem())
                .author(comment.getAuthor())
                .created(comment.getCreated())
                .build();
    }

    private ItemComment getItemComment(Comment comment) {
        return ItemComment.builder()
                .text(comment.getText())
                .build();
    }
}
