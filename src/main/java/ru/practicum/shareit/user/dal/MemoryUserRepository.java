package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoryUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();

    private long currentId = 0;

    @Override
    public User save(User user) {
        user.setId(currentId);
        users.add(user);
        currentId++;
        return user;
    }

    @Override
    public boolean containsEmail(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public User update(long id, User update) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findAny()
                .map(toUpdate -> {
                    updateData(toUpdate, update);
                    return toUpdate;
                })
                .orElse(null);
    }

    private void updateData(User user, User update) {
        if (update.getName() != null) {
            user.setName(update.getName());
        }
        if (update.getEmail() != null) {
            user.setEmail(update.getEmail());
        }
    }

    @Override
    public User get(long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean isAbsentId(long id) {
        return users.stream()
                .noneMatch(user -> user.getId() == id);
    }

    @Override
    public void delete(long id) {
        users.removeIf(user -> user.getId() == id);
    }
}
