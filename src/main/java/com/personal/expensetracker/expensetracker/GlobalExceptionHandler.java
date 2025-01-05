package com.personal.expensetracker.expensetracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(new APIResponse<>(false, "Validation failed", null, errors.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericExceptions(Exception e){
        return ResponseEntity.internalServerError().body(new APIResponse<>(false,"An error occurred while processing", null, e.getMessage()));
    }
}