package com.example.hyuk_blog.dto.login;

import lombok.Getter;

@Getter
public class TokenResponseDto {
    private final String accessToken;

    public TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
} 