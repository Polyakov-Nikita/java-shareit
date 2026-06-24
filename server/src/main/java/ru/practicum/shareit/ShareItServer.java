package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServer {
    public static final String HEADER_SHARER = "X-Sharer-User-Id";

    public static void main(String[] args) {
        SpringApplication.run(ShareItServer.class, args);
    }
}
