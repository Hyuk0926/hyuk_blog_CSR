package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/info")
    public UserDto getUserInfo(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("user");
        if (user != null) {
            // 비밀번호는 제외하고 반환
            user.setPassword(null);
            return user;
        }
        return null;
    }
} 