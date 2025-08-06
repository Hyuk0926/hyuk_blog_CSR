package com.example.hyuk_blog.service;

import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.entity.Admin;
import com.example.hyuk_blog.repository.UserRepository;
import com.example.hyuk_blog.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 조회 시도: 사용자명={}", username);
        
        // 1. User 테이블에서 사용자 찾기
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            log.debug("사용자 테이블에서 사용자 발견: 사용자명={}, 활성상태={}", username, user.isActive());
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
        
        // 2. User 테이블에 없으면 Admin 테이블에서 확인
        log.debug("사용자 테이블에서 사용자를 찾을 수 없음, 관리자 테이블 확인: 사용자명={}", username);
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없음: 사용자명={}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        
        log.debug("관리자 테이블에서 사용자 발견: 사용자명={}, 활성상태={}", username, admin.isActive());
        
        return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }
} 