package com.relex.messenger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ExceptionHandlerController {

    // Обработка IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>("Ошибка запроса: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Обработка всех других исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("Внутренняя ошибка: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
