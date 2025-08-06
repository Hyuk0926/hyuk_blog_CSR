package com.example.hyuk_blog.dto;

import com.example.hyuk_blog.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 기존 세션 관리 및 내부 처리를 위한 AdminDto
 * 보안을 위해 password 필드는 내부 처리 시에만 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private Long id;
    private String username;
    private String password; // 내부 처리용, 외부 노출 시 주의
    private String name;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Entity를 DTO로 변환 (세션용)
    public static AdminDto fromEntity(Admin admin) {
        AdminDto dto = new AdminDto();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setPassword(admin.getPassword());
        dto.setName(admin.getName());
        dto.setEmail(admin.getEmail());
        dto.setActive(admin.isActive());
        dto.setCreatedAt(admin.getCreatedAt());
        dto.setUpdatedAt(admin.getUpdatedAt());
        return dto;
    }
    
    // Entity를 DTO로 변환 (응답용 - 비밀번호 제외)
    public static AdminDto fromEntityForResponse(Admin admin) {
        AdminDto dto = new AdminDto();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setPassword(null); // 보안을 위해 비밀번호 제외
        dto.setName(admin.getName());
        dto.setEmail(admin.getEmail());
        dto.setActive(admin.isActive());
        dto.setCreatedAt(admin.getCreatedAt());
        dto.setUpdatedAt(admin.getUpdatedAt());
        return dto;
    }
    
    // DTO를 Entity로 변환
    public Admin toEntity() {
        Admin admin = new Admin();
        admin.setId(this.id);
        admin.setUsername(this.username);
        admin.setPassword(this.password);
        admin.setName(this.name);
        admin.setEmail(this.email);
        admin.setActive(this.active);
        return admin;
    }
} 