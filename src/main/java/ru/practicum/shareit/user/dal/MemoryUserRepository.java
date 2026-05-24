package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private long currentId = 0;

    @Override
    public User save(User user) {
        user.setId(currentId);
        users.put(currentId, user);
        currentId++;
        return user;
    }

    @Override
    public boolean containsEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public User update(long id, User update) {
        User toUpdate = users.get(id);
        updateData(toUpdate, update);
        return toUpdate;
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
        checkId(id);
        return users.get(id);
    }

    private void checkId(long id) {
        if (isAbsentId(id)) {
            throw new NotFoundException(User.OBJECT_TYPE, id);
        }
    }

    @Override
    public boolean isAbsentId(long id) {
        return !users.containsKey(id);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }
}
