package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSharerException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dal.UserRepository;

@SuppressWarnings("unused")
public class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createItem_ExistingSharer_NoExceptions() {
        Assertions.assertThatCode(() -> itemService.createItem(1, null))
                .doesNotThrowAnyException();
    }

    @Test
    public void updateItem_ExistingItem_ExistingSharer_NoExceptions() {
        Assertions.assertThatCode(() -> itemService.updateItem(1, 1, null))
                .doesNotThrowAnyException();
    }

    @Test
    public void updateItem_AbsentItem_NotFoundException() {
        Mockito.doReturn(true)
                .when(itemRepository)
                .isAbsentId(Mockito.any(long.class));
        Assertions.assertThatThrownBy(() -> itemService.updateItem(1, 1, null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateItem_UserIsNotSharer_NotSharerException() {
        Mockito.doReturn(true)
                .when(itemRepository)
                .isNotSharer(Mockito.any(long.class),
                        Mockito.any(long.class));
        Assertions.assertThatThrownBy(() -> itemService.updateItem(1, 1, null))
                .isInstanceOf(NotSharerException.class);
    }

    @Test
    public void getItem_ExistingItem_NoExceptions() {
        Assertions.assertThatCode(() -> itemService.getItem(1, 1))
                .doesNotThrowAnyException();
    }

    @Test
    public void getItem_AbsentItem_NotFoundException() {
        Mockito.doReturn(true)
                .when(itemRepository)
                .isAbsentId(Mockito.any(long.class));
        Assertions.assertThatThrownBy(() -> itemService.getItem(1, 1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getAllItems_ExistingSharer_NoExceptions() {
        Assertions.assertThatCode(() -> itemService.getAllItems(1))
                .doesNotThrowAnyException();
    }

    @Test
    public void getAllItems_AbsentSharer_NotFoundException() {
        Mockito.doReturn(true)
                .when(userRepository)
                .isAbsentId(Mockito.any(long.class));
        Assertions.assertThatThrownBy(() -> itemService.getAllItems(1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void searchItems_EmptyText_ReturnsEmptyArray() {
        Assertions.assertThat(itemService.searchItems(""))
                .isEmpty();
    }
}
