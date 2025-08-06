package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.entity.Admin;
import com.example.hyuk_blog.service.UserService;
import com.example.hyuk_blog.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserApiController {

    private final UserService userService;
    private final AdminRepository adminRepository;



    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getName())) {
                return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
            }

            String username = authentication.getName();
            
            // 1. User 테이블에서 사용자 찾기
            User user = userService.findByUsername(username);
            if (user != null) {
                UserDto userDto = UserDto.fromEntity(user);
                return ResponseEntity.ok(userDto);
            }
            
            // 2. User 테이블에 없으면 Admin 테이블에서 확인
            Admin admin = adminRepository.findByUsername(username).orElse(null);
            if (admin != null) {
                Map<String, Object> adminInfo = new HashMap<>();
                adminInfo.put("username", admin.getUsername());
                adminInfo.put("name", admin.getName());
                adminInfo.put("email", admin.getEmail());
                adminInfo.put("role", "ROLE_ADMIN");
                adminInfo.put("active", admin.isActive());
                return ResponseEntity.ok(adminInfo);
            }

            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("사용자 정보 조회 중 오류가 발생했습니다.");
        }
    }
} 