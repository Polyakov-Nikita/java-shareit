package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@SuppressWarnings("unused")
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unexpected(Throwable e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("GATEWAY: произошла непредвиденная ошибка", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> methodArgumentNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        if (fieldError == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Некорректное значение параметра", ""));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Некорректное значение параметра",
                        String.format("Значение параметра %s=%s некорректно, причина: %s",
                                fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage())
                ));
    }
}
