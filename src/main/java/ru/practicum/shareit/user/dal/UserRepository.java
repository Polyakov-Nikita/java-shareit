package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.User;

public interface UserRepository {
    User save(User user);

    boolean containsEmail(String email);

    User update(long id, User update);

    User get(long id);

    boolean isAbsentId(long id);

    void delete(long id);
}
