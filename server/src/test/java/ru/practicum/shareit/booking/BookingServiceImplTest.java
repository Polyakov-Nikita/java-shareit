package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.Booker;
import ru.practicum.shareit.booking.dto.BookingItem;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BookingDatesException;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.test.ServiceTest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class BookingServiceImplTest extends ServiceTest {
    private final BookingMapper bookingMapper = new BookingMapper();

    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(userRepository, itemRepository, null,
                bookingMapper,
                bookingRepository);
    }

    @Test
    public void createBooking_ReturnsObject() {
        // Arrange
        CreateBookingRequest request = buildCreateBookingRequest();
        User booker = buildUser(ID);
        Item item = buildItem(ID, booker);
        whenFound(booker);
        whenFound(item);
        whenSaveReturns(bookingRepository, this::saveBooking);

        // Act
        BookingResponse actual = bookingService.createBooking(booker.getId(), request);

        // Assert
        BookingResponse expected = buildBookingResponse(request, booker, item);
        assertEquals(actual, expected);
    }

    @Test
    public void createBooking_StartEqualsEnd_BookingDatesException() {
        // Arrange
        CreateBookingRequest request = buildCreateBookingEqualDatesRequest();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.createBooking(ID, request));

        // Assert
        assertBookingDatesException(thrown);
    }

    @Test
    public void createBooking_AbsentBooker_NotFoundException() {
        // Arrange
        CreateBookingRequest request = buildCreateBookingRequest();
        whenUserNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.createBooking(ID, request));

        // Assert
        assertNotFoundException(thrown);
    }

    @Test
    public void createBooking_AbsentItem_NotFoundException() {
        // Arrange
        CreateBookingRequest request = buildCreateBookingRequest();
        User booker = buildUser(ID);
        whenFound(booker);
        whenItemNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.createBooking(ID, request));

        // Assert
        assertNotFoundException(thrown);
    }

    @Test
    public void createBooking_NotAvailableItem_NotAvailableException() {
        // Arrange
        CreateBookingRequest request = buildCreateBookingRequest();
        User booker = buildUser(ID);
        Item item = buildNotAvailableItem(booker);
        whenFound(booker);
        whenFound(item);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.createBooking(ID, request));

        // Assert
        assertNotAvailableException(thrown);
    }

    @Test
    public void approveBooking_Approved_ReturnsObject() {
        // Arrange
        User booker = buildUser(ID);
        Item item = buildItem(ID, booker);
        Booking booking = buildBooking(item, booker);
        whenFound(booking);
        whenSaveReturns(bookingRepository, this::saveBooking);

        // Act
        BookingResponse actual = bookingService.approveBooking(booking.getId(), booker.getId(), true);

        // Assert
        BookingResponse expected = buildBookingResponse(booking, BookingStatus.APPROVED);
        assertEquals(actual, expected);
    }

    @Test
    public void approveBooking_Rejected_ReturnsObject() {
        // Arrange
        User booker = buildUser(ID);
        Item item = buildItem(ID, booker);
        Booking booking = buildBooking(item, booker);
        whenFound(booking);
        whenSaveReturns(bookingRepository, this::saveBooking);

        // Act
        BookingResponse actual = bookingService.approveBooking(booking.getId(), booker.getId(), false);

        // Assert
        BookingResponse expected = buildBookingResponse(booking, BookingStatus.REJECTED);
        assertEquals(actual, expected);
    }

    @Test
    public void approveBooking_AbsentBooking_NotFoundException() {
        // Arrange
        User booker = buildUser(ID);
        whenBookingNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.approveBooking(ID, booker.getId(), true));

        // Assert
        assertNotFoundException(thrown);
    }

    @Test
    public void approveBooking_NotOwner_NotOwnerException() {
        // Arrange
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        User sharer = buildUser(ID + 1);
        Booking booking = buildBooking(item, sharer);
        whenFound(booking);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.approveBooking(ID, sharer.getId(), true));

        // Assert
        assertNotOwnerException(thrown);
    }

    @Test
    public void getBooking_Owner_ReturnsObject() {
        // Arrange
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        User booker = buildUser(ID + 1);
        Booking booking = buildBooking(item, booker);
        whenFound(booking);

        // Act
        BookingResponse actual = bookingService.getBooking(booking.getId(), owner.getId());

        // Assert
        BookingResponse expected = buildBookingResponse(booking);
        assertEquals(actual, expected);
    }

    @Test
    public void getBooking_Booker_ReturnsObject() {
        // Arrange
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        User booker = buildUser(ID + 1);
        Booking booking = buildBooking(item, booker);
        whenFound(booking);

        // Act
        BookingResponse actual = bookingService.getBooking(booking.getId(), booker.getId());

        // Assert
        BookingResponse expected = buildBookingResponse(booking);
        assertEquals(actual, expected);
    }

    @Test
    public void getBooking_AbsentBooking_NotFoundException() {
        // Arrange
        whenBookingNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.getBooking(ID, ID));

        // Assert
        assertNotFoundException(thrown);
    }

    @Test
    public void getBooking_ThirdParty_ForbiddenAccessException() {
        // Arrange
        User owner = buildUser(ID);
        Item item = buildItem(ID, owner);
        User booker = buildUser(ID + 1);
        Booking booking = buildBooking(item, booker);
        whenFound(booking);

        // Act
        long thirdPartyId = ID + 2;
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.getBooking(booking.getId(), thirdPartyId));

        // Assert
        assertForbiddenAccessException(thrown);
    }

    @Test
    public void getAllUserBookings_All_ReturnsArray() {
        runGetAllBookingsTest(BookingSearchState.ALL, this::whenAllBookingsOf);
    }

    @Test
    public void getAllUserBookings_Current_ReturnsArray() {
        runGetAllBookingsTest(BookingSearchState.CURRENT, this::whenCurrentBookingsOf);
    }

    @Test
    public void getAllUserBookings_Past_ReturnsArray() {
        runGetAllBookingsTest(BookingSearchState.PAST, this::whenPastBookingsOf);
    }

    @Test
    public void getAllUserBookings_Future_ReturnsArray() {
        runGetAllBookingsTest(BookingSearchState.FUTURE, this::whenFutureBookingsOf);
    }

    @Test
    public void getAllUserBookings_Waiting_ReturnsArray() {
        runGetAllBookingsTest(BookingSearchState.WAITING, this::whenWaitingBookingsOf);
    }

    @Test
    public void getAllUserBookings_Rejected_ReturnsArray() {
        runGetAllBookingsTest(BookingSearchState.REJECTED, this::whenRejectedBookingsOf);
    }

    @Test
    public void getAllUserItemBookings_All_ReturnsArray() {
        runGetAllUserItemBookingsTest(BookingSearchState.ALL, this::whenAllUserItemBookingsOf);
    }

    @Test
    public void getAllUserItemBookings_Current_ReturnsArray() {
        runGetAllUserItemBookingsTest(BookingSearchState.CURRENT, this::whenCurrentUserItemBookingsOf);
    }

    @Test
    public void getAllUserItemBookings_Past_ReturnsArray() {
        runGetAllUserItemBookingsTest(BookingSearchState.PAST, this::whenPastUserItemBookingsOf);
    }

    @Test
    public void getAllUserItemBookings_Future_ReturnsArray() {
        runGetAllUserItemBookingsTest(BookingSearchState.FUTURE, this::whenFutureUserItemBookingsOf);
    }

    @Test
    public void getAllUserItemBookings_Waiting_ReturnsArray() {
        runGetAllUserItemBookingsTest(BookingSearchState.WAITING, this::whenWaitingUserItemBookingsOf);
    }

    @Test
    public void getAllUserItemBookings_Rejected_ReturnsArray() {
        runGetAllUserItemBookingsTest(BookingSearchState.REJECTED, this::whenRejectedUserItemBookingsOf);
    }

    @Test
    public void getAllUserItemBookings_AbsentSharer_NotFoundException() {
        // Arrange
        whenUserNotFound();

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> bookingService.getAllUserItemBookings(ID, BookingSearchState.ALL));

        // Assert
        assertNotFoundException(thrown);
    }

    private void runGetAllBookingsTest(BookingSearchState state, BiConsumer<User, List<Booking>> mockSetup) {
        // Arrange
        User booker = buildUser(ID);
        Item item1 = buildItem(ID, booker);
        Item item2 = buildItem(ID + 1, booker);
        Booking booking1 = buildBooking(item1, booker);
        Booking booking2 = buildBooking(item2, booker);
        List<Booking> bookings = List.of(
                booking1,
                booking2
        );
        mockSetup.accept(booker, bookings);

        // Act
        List<BookingResponse> actual = bookingService.getAllUserBookings(booker.getId(), state);

        // Assert
        List<BookingResponse> expected = List.of(
                buildBookingResponse(booking1),
                buildBookingResponse(booking2)
        );
        assertEquals(actual, expected);
    }

    private void runGetAllUserItemBookingsTest(BookingSearchState state, BiConsumer<User, List<Booking>> mockSetup) {
        // Arrange
        User booker = buildUser(ID);
        Item item1 = buildItem(ID, booker);
        Item item2 = buildItem(ID + 1, booker);
        Booking booking1 = buildBooking(item1, booker);
        Booking booking2 = buildBooking(item2, booker);
        List<Booking> bookings = List.of(
                booking1,
                booking2
        );
        whenFound(booker);
        mockSetup.accept(booker, bookings);

        // Act
        List<BookingResponse> actual = bookingService.getAllUserItemBookings(booker.getId(), state);

        // Assert
        List<BookingResponse> expected = List.of(
                buildBookingResponse(booking1),
                buildBookingResponse(booking2)
        );
        assertEquals(actual, expected);
    }

    private void assertBookingDatesException(Throwable throwable) {
        Assertions.assertThat(throwable)
                .isInstanceOf(BookingDatesException.class);
    }

    private void assertNotAvailableException(Throwable throwable) {
        Assertions.assertThat(throwable)
                .isInstanceOf(NotAvailableException.class);
    }

    private void assertForbiddenAccessException(Throwable throwable) {
        Assertions.assertThat(throwable)
                .isInstanceOf(ForbiddenAccessException.class);
    }

    private void whenFound(Booking booking) {
        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
    }

    private void whenBookingNotFound() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
    }

    private void whenAllBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findByBookerId(Mockito.eq(booker.getId()), Mockito.any(Sort.class)))
                .thenReturn(bookings);
    }

    private void whenCurrentBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                        Mockito.eq(booker.getId()),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Sort.class)
                ))
                .thenReturn(bookings);
    }

    private void whenPastBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findByBookerIdAndEndBefore(
                        Mockito.eq(booker.getId()),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Sort.class)))
                .thenReturn(bookings);
    }

    private void whenFutureBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findByBookerIdAndStartAfter(
                        Mockito.eq(booker.getId()),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Sort.class)))
                .thenReturn(bookings);
    }

    private void whenWaitingBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findByBookerIdAndStatus(
                        Mockito.eq(booker.getId()),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Sort.class)))
                .thenReturn(bookings);
    }

    private void whenRejectedBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findByBookerIdAndStatus(
                        Mockito.eq(booker.getId()),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Sort.class)))
                .thenReturn(bookings);
    }

    private void whenAllUserItemBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findItemBookingsByOwnerId(Mockito.eq(booker.getId())))
                .thenReturn(bookings);
    }

    private void whenCurrentUserItemBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findCurrentItemBookingsByOwnerId(Mockito.eq(booker.getId()), Mockito.any(LocalDateTime.class)))
                .thenReturn(bookings);
    }

    private void whenPastUserItemBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findPastItemBookingsByOwnerId(Mockito.eq(booker.getId()), Mockito.any(LocalDateTime.class)))
                .thenReturn(bookings);
    }

    private void whenFutureUserItemBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findFutureItemBookingsByOwnerId(Mockito.eq(booker.getId()), Mockito.any(LocalDateTime.class)))
                .thenReturn(bookings);
    }

    private void whenWaitingUserItemBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findItemBookingsByOwnerIdAndStatus(Mockito.eq(booker.getId()), Mockito.eq(BookingStatus.WAITING)))
                .thenReturn(bookings);
    }

    private void whenRejectedUserItemBookingsOf(User booker, List<Booking> bookings) {
        Mockito.when(bookingRepository.findItemBookingsByOwnerIdAndStatus(Mockito.eq(booker.getId()), Mockito.eq(BookingStatus.REJECTED)))
                .thenReturn(bookings);
    }

    private CreateBookingRequest buildCreateBookingRequest() {
        return CreateBookingRequest.builder()
                .itemId(ID)
                .start(NOW.minusDays(1))
                .end(NOW.plusDays(1))
                .build();
    }

    private CreateBookingRequest buildCreateBookingEqualDatesRequest() {
        return CreateBookingRequest.builder()
                .itemId(ID)
                .start(NOW)
                .end(NOW)
                .build();
    }

    private BookingResponse buildBookingResponse(CreateBookingRequest request, User booker, Item item) {
        return BookingResponse.builder()
                .id(ID)
                .start(request.getStart())
                .end(request.getEnd())
                .booker(getBookerFrom(booker))
                .item(getBookingItemFrom(item))
                .status(BookingStatus.WAITING)
                .build();
    }

    private Booker getBookerFrom(User booker) {
        return Booker.builder()
                .id(booker.getId())
                .build();
    }

    private BookingItem getBookingItemFrom(Item item) {
        return BookingItem.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    private BookingResponse buildBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(getBookerFrom(booking.getBooker()))
                .item(getBookingItemFrom(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    private BookingResponse buildBookingResponse(Booking booking, BookingStatus status) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(getBookerFrom(booking.getBooker()))
                .item(getBookingItemFrom(booking.getItem()))
                .status(status)
                .build();
    }

    private Item buildNotAvailableItem(User owner) {
        return Item.builder()
                .id(ID)
                .name("Name")
                .description("Description")
                .owner(owner)
                .available(false)
                .build();
    }

    private Booking saveBooking(Booking booking) {
        return Booking.builder()
                .id(ID)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }
}
