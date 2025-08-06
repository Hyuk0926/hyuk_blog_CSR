package com.example.hyuk_blog.dto.admin;

import com.example.hyuk_blog.entity.Admin;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminResponseDto {
    private final Long id;
    private final String username;
    private final String name;
    private final String email;
    private final boolean active;
    private final LocalDateTime createdAt;

    // Entity를 DTO로 변환하는 생성자
    public AdminResponseDto(Admin admin) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.name = admin.getName();
        this.email = admin.getEmail();
        this.active = admin.isActive();
        this.createdAt = admin.getCreatedAt();
    }
} 