package ru.practicum.shareit.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    protected static final long ID = 1L;
    protected static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ItemRepository itemRepository;
    @Mock
    protected ItemRequestRepository itemRequestRepository;

    protected <T> void assertEquals(T actual, T expected) {
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    protected <T> void assertEqualsIgnoring(T actual, T expected, String ignoringFieldName) {
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(ignoringFieldName)
                .isEqualTo(expected);
    }

    protected void assertNotFoundException(Throwable throwable) {
        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class);
    }

    protected void assertNotOwnerException(Throwable throwable) {
        Assertions.assertThat(throwable)
                .isInstanceOf(NotOwnerException.class);
    }

    protected <M> void assertMethodCall(M mock, Consumer<M> methodCall) {
        methodCall.accept(Mockito.verify(
                mock,
                Mockito.times(1))
        );
    }

    protected <M> void assertMethodNotCall(M mock, Consumer<M> methodCall) {
        methodCall.accept(Mockito.verify(
                mock,
                Mockito.never())
        );
    }

    protected <M> void assertMethodsNotCall(M mock, List<Consumer<M>> methodCalls) {
        for (Consumer<M> methodCall : methodCalls) {
            methodCall.accept(Mockito.verify(
                    mock,
                    Mockito.never())
            );
        }
    }

    protected <T> void whenSaveReturns(JpaRepository<T, Long> repository, Function<T, T> saveAnswer) {
        Mockito.when(repository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> saveAnswer.apply(invocationOnMock.getArgument(0)));
    }

    protected void whenFound(User user) {
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
    }

    protected void whenFound(Item item) {
        Mockito.when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
    }

    protected void whenFound(ItemRequest request) {
        Mockito.when(itemRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));
    }

    protected void whenUserNotFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
    }

    protected void whenItemNotFound() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
    }

    protected void whenItemRequestNotFound() {
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
    }

    protected User buildUser(long id) {
        return User.builder()
                .id(id)
                .name("Name")
                .email("email@example.com")
                .build();
    }

    protected Item buildItem(long id, User owner) {
        return Item.builder()
                .id(id)
                .name("Name")
                .description("Description")
                .owner(owner)
                .available(true)
                .build();
    }

    protected Booking buildBooking(Item item, User booker) {
        LocalDateTime now = LocalDateTime.now();
        return Booking.builder()
                .id(ID)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    protected ItemRequest buildItemRequest(long id, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .id(id)
                .description("Description " + id)
                .requestor(requestor)
                .created(created)
                .build();
    }
}
