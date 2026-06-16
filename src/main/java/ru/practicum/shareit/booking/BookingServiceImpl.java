package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ServiceBase;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl extends ServiceBase implements BookingService {
    private final BookingMapper mapper;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(UserRepository userRepository, ItemRepository itemRepository,
                              BookingMapper mapper,
                              BookingRepository bookingRepository) {
        super(userRepository, itemRepository);
        this.mapper = mapper;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(long sharerId, CreateBookingRequest request) {
        checkCreateRequest(request);
        User booker = findUser(sharerId);
        Item item = findItem(request.getItemId());
        checkItemAvailable(item);
        Booking booking = mapper.toBooking(item, booker, request);
        booking.setStatus(BookingStatus.WAITING);
        Booking result = bookingRepository.save(booking);
        return mapper.toBookingResponse(result);
    }

    private void checkCreateRequest(CreateBookingRequest request) {
        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();
        if (!start.isBefore(end)) {
            throw new BookingDatesException(start, end);
        }
    }

    private void checkItemAvailable(Item item) {
        if (!item.isAvailable()) {
            throw new NotAvailableException(item.getId());
        }
    }

    @Override
    @Transactional
    public BookingResponse approveBooking(long id, long sharerId, boolean approved) {
        Booking booking = findBooking(id);
        checkOwner(booking, sharerId);
        approve(booking, approved);
        Booking result = bookingRepository.save(booking);
        return mapper.toBookingResponse(result);
    }

    private Booking findBooking(long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("бронирование", id));
    }

    private void checkOwner(Booking booking, long sharerId) {
        Item item = booking.getItem();
        if (item.getOwner().getId() != sharerId) {
            throw new NotOwnerException(sharerId, item.getId());
        }
    }

    private void approve(Booking booking, boolean approved) {
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }

    @Override
    public BookingResponse getBooking(long id, long sharerId) {
        Booking result = findBooking(id);
        checkOwnerOrBooker(result, sharerId);
        return mapper.toBookingResponse(result);
    }

    private void checkOwnerOrBooker(Booking booking, long sharerId) {
        Item item = booking.getItem();
        if (booking.getBooker().getId() != sharerId && item.getOwner().getId() != sharerId) {
            throw new ForbiddenAccessException(sharerId, "не является автором бронирования или владельцем вещи");
        }
    }

    @Override
    public List<BookingResponse> getAllUserBookings(long sharerId, BookingSearchState state) {
        List<Booking> result = searchBookings(sharerId, state);
        return result.stream()
                .map(mapper::toBookingResponse)
                .toList();
    }

    private List<Booking> searchBookings(long sharerId, BookingSearchState state) {
        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(sharerId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(sharerId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(sharerId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(sharerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(sharerId, BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(sharerId);
        };
    }

    @Override
    public List<BookingResponse> getAllUserItemBookings(long sharerId, BookingSearchState state) {
        findUser(sharerId);
        List<Booking> result = searchItemBookings(sharerId, state);
        return result.stream()
                .map(mapper::toBookingResponse)
                .toList();
    }

    private List<Booking> searchItemBookings(long sharerId, BookingSearchState state) {
        return switch (state) {
            case CURRENT -> bookingRepository.findCurrentItemBookingsByOwnerId(sharerId, LocalDateTime.now());
            case PAST -> bookingRepository.findPastItemBookingsByOwnerId(sharerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureItemBookingsByOwnerId(sharerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findItemBookingsByOwnerIdAndStatus(sharerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findItemBookingsByOwnerIdAndStatus(sharerId, BookingStatus.REJECTED);
            default -> bookingRepository.findItemBookingsByOwnerId(sharerId);
        };
    }
}
