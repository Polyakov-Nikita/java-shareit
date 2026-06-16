package ru.practicum.shareit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    protected static final long ID = 1L;
    protected static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ItemRepository itemRepository;

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

    protected void whenUserNotFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
    }

    protected void whenItemNotFound() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
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
}
