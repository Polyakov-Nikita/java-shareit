package ru.practicum.shareit.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@org.springframework.boot.test.autoconfigure.json.JsonTest
@SuppressWarnings("unused")
public class JsonTest<T> {
    protected static final LocalDateTime NOW = LocalDateTime.now();
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private JacksonTester<T> jacksonTester;

    protected JsonContent<T> writeContent(T object) {
        try {
            return jacksonTester.write(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
