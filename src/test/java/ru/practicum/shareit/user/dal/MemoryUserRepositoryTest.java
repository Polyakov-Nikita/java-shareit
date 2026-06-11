package ru.practicum.shareit.user.dal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

public class MemoryUserRepositoryTest {
    private static final MemoryUserRepository REPOSITORY = new MemoryUserRepository();

    @Test
    public void save_ReturnsObject() {
        User saved = REPOSITORY.save(buildUser("user"));
        User received = REPOSITORY.get(saved.getId());
        Assertions.assertThat(received).isEqualTo(saved);
    }

    private User buildUser(String prefix) {
        return User.builder()
                .name(prefix + "name")
                .email(prefix + "mail@example.com")
                .build();
    }


    @Test
    public void containsEmail_AbsentEmail_False() {
        String absentEmail = "absentEmail@mail.com";
        Assertions.assertThat(REPOSITORY.containsEmail(absentEmail)).isFalse();
    }

    @Test
    public void containsEmail_ExistingEmail_True() {
        User existingEmailHolder = buildUser("emailHolder");
        REPOSITORY.save(existingEmailHolder);
        Assertions.assertThat(REPOSITORY.containsEmail(existingEmailHolder.getEmail())).isTrue();
    }

    @Test
    public void update_ReturnsObject() {
        long savedId = REPOSITORY.save(buildUser("previous")).getId();
        User update = buildUserUpdate(savedId);
        REPOSITORY.update(savedId, update);
        User updated = REPOSITORY.get(savedId);
        Assertions.assertThat(updated).isEqualTo(update);
    }

    private User buildUserUpdate(long id) {
        return User.builder()
                .id(id)
                .name("Name Update")
                .email("mailupdate@example.com")
                .build();
    }

    @Test
    public void get_ReturnsObject() {
        User saved = REPOSITORY.save(buildUser("saved"));
        User received = REPOSITORY.get(saved.getId());
        Assertions.assertThat(received).isEqualTo(saved);
    }

    @Test
    public void get_AbsentUser_NotFoundException() {
        long absentId = 9999;
        Assertions.assertThatThrownBy(() -> REPOSITORY.get(absentId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void isAbsentId_ExistingId_False() {
        long existingId = REPOSITORY.save(buildUser("existingIdHolder")).getId();
        Assertions.assertThat(REPOSITORY.isAbsentId(existingId)).isFalse();
    }

    @Test
    public void isAbsentId_AbsentId_True() {
        long absentId = 99999;
        Assertions.assertThat(REPOSITORY.isAbsentId(absentId)).isTrue();
    }

    @Test
    public void delete_NoObject() {
        long savedId = REPOSITORY.save(buildUser("toDelete")).getId();
        REPOSITORY.delete(savedId);
        Assertions.assertThatThrownBy(() -> REPOSITORY.get(savedId))
                .isInstanceOf(NotFoundException.class);
    }
}
