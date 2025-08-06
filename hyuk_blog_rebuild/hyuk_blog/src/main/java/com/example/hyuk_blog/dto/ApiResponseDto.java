package com.example.hyuk_blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    
    // 성공 응답 생성
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, "성공", data, null);
    }
    
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(true, message, data, null);
    }
    
    // 실패 응답 생성
    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, message, null, null);
    }
    
    public static <T> ApiResponseDto<T> error(String message, String errorCode) {
        return new ApiResponseDto<>(false, message, null, errorCode);
    }
} 