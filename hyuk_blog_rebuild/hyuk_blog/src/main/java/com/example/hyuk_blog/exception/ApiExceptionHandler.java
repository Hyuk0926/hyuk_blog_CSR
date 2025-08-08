package com.example.hyuk_blog.exception;

import com.example.hyuk_blog.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(basePackages = "com.example.hyuk_blog.api.controller")
public class ApiExceptionHandler {

    // Logger 객체 생성 (이 클래스에서 로그를 남기기 위함)
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * 404 Not Found 예외 처리
     * 리소스를 찾을 수 없을 때 발생하는 예외
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        // 어떤 예외가 발생했는지 콘솔에 에러 로그를 남깁니다. (스택 트레이스 포함)
        log.error("404 Not Found: 요청한 리소스를 찾을 수 없습니다.", e);
        
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
        log.error("403 Forbidden: 접근 권한이 없습니다.", e);

        ErrorResponse errorResponse = new ErrorResponse(
            "FORBIDDEN",
            "접근 권한이 없습니다: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * 500 Internal Server Error 예외 처리 (가장 중요!)
     * 예상치 못한 모든 예외를 처리합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        // 어떤 종류의 예외인지, 어느 코드에서 발생했는지 정확히 알 수 있도록 전체 내용을 출력합니다.
        log.error("500 Internal Server Error: 서버 내부 오류가 발생했습니다.", e);

        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
