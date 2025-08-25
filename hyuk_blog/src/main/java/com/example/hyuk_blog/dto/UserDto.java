package com.example.hyuk_blog.dto;

import com.example.hyuk_blog.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String profileImage;
    private String bio; // 자기소개
    private String backgroundImage; // 배경화면 이미지 URL
    private String backgroundColor; // 배경색
    private String backgroundStyle; // 배경 스타일
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Entity를 DTO로 변환
    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setProfileImage(user.getProfileImage());
        dto.setBio(user.getBio());
        dto.setBackgroundImage(user.getBackgroundImage());
        dto.setBackgroundColor(user.getBackgroundColor());
        dto.setBackgroundStyle(user.getBackgroundStyle());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    
    // DTO를 Entity로 변환
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setNickname(this.nickname);
        user.setEmail(this.email);
        user.setProfileImage(this.profileImage);
        user.setBio(this.bio);
        user.setBackgroundImage(this.backgroundImage);
        user.setBackgroundColor(this.backgroundColor);
        user.setBackgroundStyle(this.backgroundStyle);
        user.setActive(this.active);
        return user;
    }
} 