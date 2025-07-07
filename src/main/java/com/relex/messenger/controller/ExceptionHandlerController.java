package com.relex.messenger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String message;
        switch (status) {
            case NOT_FOUND -> message = "Объект не найден: " + ex.getReason();
            case CONFLICT -> message = "Конфликт данных: " + ex.getReason();
            case BAD_REQUEST -> message = "Ошибка запроса: " + ex.getReason();
            case FORBIDDEN -> message = "У Вас нет прав на выполнение данного действия: " + ex.getReason();
            case UNAUTHORIZED -> message = "Неавторизованный доступ: " + ex.getReason();
            default -> message = "Неизвестный статус: " + ex.getReason();
        }

        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return new ResponseEntity<>("Ошибка ввода-вывода при передаче данных: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>("Неверно переданы данные: " + ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>("Неверный пароль: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("Что-то пошло не так: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
