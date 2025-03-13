package com.umaxcode.springboot_with_aws_transcribe.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AudioTranscriptionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse audioTranscriptionExceptionHandler(AudioTranscriptionException ex, HttpServletRequest request) {

        return ErrorResponse.builder()
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse genericExceptionHandler(Exception ex, HttpServletRequest request) {

        return ErrorResponse.builder()
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
