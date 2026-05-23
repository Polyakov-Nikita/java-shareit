package ru.practicum.shareit.item.dal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dal.MemoryUserRepository;

import java.util.ArrayList;
import java.util.List;

public class MemoryItemRepositoryTest {
    private final MemoryItemRepository itemRepository = new MemoryItemRepository();
    private final MemoryUserRepository userRepository = new MemoryUserRepository();

    @Test
    public void save_ReturnsObject() {
        Item saved = itemRepository.save(buildItem("item"));
        Item received = itemRepository.get(saved.getId());
        Assertions.assertThat(received).isEqualTo(saved);
    }

    private Item buildItem(String prefix) {
        return Item.builder()
                .name(prefix + "name")
                .description(prefix + " description")
                .itemStatus(Item.ItemStatus.AVAILABLE)
                .build();
    }

    @Test
    public void update_ReturnsObject() {
        long savedId = itemRepository.save(buildItem("previous")).getId();
        Item update = buildItemUpdate(savedId);
        itemRepository.update(savedId, update);
        Item updated = itemRepository.get(savedId);
        Assertions.assertThat(updated).isEqualTo(update);
    }

    private Item buildItemUpdate(long id) {
        return Item.builder()
                .id(id)
                .name("Name Update")
                .description("Description Update")
                .itemStatus(Item.ItemStatus.OCCUPIED)
                .build();
    }

    @Test
    public void get_ReturnsObject() {
        Item saved = itemRepository.save(buildItem("saved"));
        Item received = itemRepository.get(saved.getId());
        Assertions.assertThat(received).isEqualTo(saved);
    }

    @Test
    public void isAbsentId_ExistingId_False() {
        long existingId = itemRepository.save(buildItem("existingIdHolder")).getId();
        Assertions.assertThat(itemRepository.isAbsentId(existingId)).isFalse();
    }

    @Test
    public void isAbsentId_AbsentId_True() {
        long absentId = 99999;
        Assertions.assertThat(itemRepository.isAbsentId(absentId)).isTrue();
    }

    @Test
    public void getAll_ReturnsArray() {
        long sharerId = userRepository.save(buildSharer("Sharer")).getId();
        int itemsCount = 3;
        List<Item> items = createItems(itemsCount, sharerId);
        saveItems(items);
        List<Item> received = itemRepository.getAll(sharerId);
        Assertions.assertThat(received).containsExactlyInAnyOrderElementsOf(items);
    }

    private User buildSharer(String prefix) {
        return User.builder()
                .name(prefix + " Name")
                .email(prefix + "mail@example.com")
                .build();
    }

    private List<Item> createItems(int count, long sharerId) {
        User sharer = userRepository.get(sharerId);
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(buildItem("item" + i, sharer));
        }
        return items;
    }

    private Item buildItem(String prefix, User sharer) {
        return Item.builder()
                .name(prefix + "name")
                .description(prefix + " description")
                .itemStatus(Item.ItemStatus.AVAILABLE)
                .owner(sharer)
                .build();
    }

    private void saveItems(List<Item> items) {
        for (Item item : items) {
            itemRepository.save(item);
        }
    }

    @Test
    public void getAll_WithOtherSharers_ReturnsArray() {
        long sharerId = userRepository.save(buildSharer("SharerToSearch")).getId();
        int sharerItemsCount = 5;
        List<Item> sharerItems = createItems(sharerItemsCount, sharerId);
        saveItems(sharerItems);
        int otherItemsCount = 2;
        long owner1Id = userRepository.save(buildSharer("owner1")).getId();
        List<Item> otherItems1 = createItems(otherItemsCount, owner1Id);
        saveItems(otherItems1);
        long owner2Id = userRepository.save(buildSharer("owner2")).getId();
        List<Item> otherItems2 = createItems(otherItemsCount, owner2Id);
        saveItems(otherItems2);
        List<Item> received = itemRepository.getAll(sharerId);
        Assertions.assertThat(received).containsExactlyInAnyOrderElementsOf(sharerItems);
    }

    @Test
    public void getAll_NoItems_ReturnsEmptyArray() {
        long sharerId = userRepository.save(buildSharer("SharerWithoutItems")).getId();
        List<Item> received = itemRepository.getAll(sharerId);
        Assertions.assertThat(received).isEmpty();
    }

    @Test
    public void search_ReturnsArray() {
        int expectedCount = 3;
        String text = "text";
        List<Item> expected = createItems(expectedCount, "someInfo" + text);
        saveItems(expected);
        int otherCount = 5;
        List<Item> other = createItems(otherCount, "other");
        saveItems(other);
        List<Item> received = itemRepository.search(text);
        Assertions.assertThat(received).containsExactlyInAnyOrderElementsOf(expected);
    }

    private List<Item> createItems(int count, String prefix) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(buildItem(prefix + i));
        }
        return items;
    }

    @Test
    public void search_ExcludeOccupied_ReturnsArray() {
        int availableCount = 3;
        List<Item> availableItems = createItems(availableCount, Item.ItemStatus.AVAILABLE);
        saveItems(availableItems);
        int occupiedCount = 2;
        List<Item> occupiedItems = createItems(occupiedCount, Item.ItemStatus.OCCUPIED);
        saveItems(occupiedItems);
        String text = "item";
        List<Item> received = itemRepository.search(text);
        Assertions.assertThat(received).containsExactlyInAnyOrderElementsOf(availableItems);
    }

    private List<Item> createItems(int count, Item.ItemStatus itemStatus) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(buildItem(itemStatus));
        }
        return items;
    }

    private Item buildItem(Item.ItemStatus status) {
        String prefix = "Item With Status " + status;
        return Item.builder()
                .name(prefix + " Name")
                .description(prefix + " description")
                .itemStatus(status)
                .build();
    }

    @Test
    public void search_EmptyText_ReturnsEmptyArray() {
        int itemsCount = 4;
        List<Item> items = createItems(itemsCount);
        saveItems(items);
        String text = "";
        List<Item> received = itemRepository.search(text);
        Assertions.assertThat(received).isEmpty();
    }

    private List<Item> createItems(int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(buildItem("someItem" + i));
        }
        return items;
    }
}
