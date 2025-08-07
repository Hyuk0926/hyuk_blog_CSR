package com.example.hyuk_blog.exception;

import com.example.hyuk_blog.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(basePackages = "com.example.hyuk_blog.api.controller")
public class ApiExceptionHandler {

    /**
     * 404 Not Found 예외 처리
     * 리소스를 찾을 수 없을 때 발생하는 예외
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            "NOT_FOUND",
            "요청한 리소스를 찾을 수 없습니다: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * 403 Forbidden 예외 처리
     * 인증은 되었지만 권한이 없을 때 발생하는 예외
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            "FORBIDDEN",
            "접근 권한이 없습니다: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * 500 Internal Server Error 예외 처리
     * 예상치 못한 모든 예외를 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
