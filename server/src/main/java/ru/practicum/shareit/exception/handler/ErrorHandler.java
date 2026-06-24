package ru.practicum.shareit.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@RestControllerAdvice
@SuppressWarnings("unused")
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unexpected(Throwable e) {
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER: произошла непредвиденная ошибка", e.getMessage());
    }

    private ResponseEntity<ErrorResponse> createResponse(HttpStatus status, String message, String details) {
        return ResponseEntity.status(status).body(new ErrorResponse(message, details));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> duplicatedData(DuplicatedDataException e) {
        return createResponse(HttpStatus.CONFLICT, "Дублирование данных", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFound(NotFoundException e) {
        return createResponse(HttpStatus.NOT_FOUND, "Ресурс не найден", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notOwner(NotOwnerException e) {
        return createResponse(HttpStatus.FORBIDDEN, "Пользователь не владелец предмета", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notAvailable(NotAvailableException e) {
        return createResponse(HttpStatus.BAD_REQUEST, "Предмет недоступен", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> bookingDates(BookingDatesException e) {
        return createResponse(HttpStatus.BAD_REQUEST, "Некорректное время бронирования", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> forbiddenAccess(ForbiddenAccessException e) {
        return createResponse(HttpStatus.FORBIDDEN, "Доступ запрещён", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> forbiddenComment(ForbiddenCommentException e) {
        return createResponse(HttpStatus.BAD_REQUEST, "Комментарий невозможен", e.getMessage());
    }
}
