package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserApiController {

    private final UserService userService;



    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getName())) {
                return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            UserDto userDto = UserDto.fromEntity(user);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("사용자 정보 조회 중 오류가 발생했습니다.");
        }
    }
} 