package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.AdminDto;
import com.example.hyuk_blog.dto.admin.AdminCreateRequestDto;
import com.example.hyuk_blog.dto.admin.AdminResponseDto;
import com.example.hyuk_blog.dto.admin.AdminUpdateRequestDto;
import com.example.hyuk_blog.entity.Admin;
import com.example.hyuk_blog.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 관리자 로그인 인증 (세션용)
    public Optional<AdminDto> authenticate(String username, String password) {
        return adminRepository.findByUsernameAndActiveTrue(username)
                .filter(admin -> {
                    // BCrypt로 비밀번호 검증 시도
                    if (passwordEncoder.matches(password, admin.getPassword())) {
                        return true;
                    }
                    
                    // BCrypt 실패 시 평문 비교 시도 (기존 admin 계정 호환성)
                    return admin.getPassword().equals(password);
                })
                .map(AdminDto::fromEntity);
    }
    
    // 사용자명으로 관리자 조회 (세션용)
    public Optional<AdminDto> findByUsername(String username) {
        return adminRepository.findByUsername(username)
                .map(AdminDto::fromEntity);
    }
    
    // 관리자 저장 (세션용)
    public AdminDto saveAdmin(AdminDto adminDto) {
        Admin admin = adminDto.toEntity();
        Admin savedAdmin = adminRepository.save(admin);
        return AdminDto.fromEntity(savedAdmin);
    }
    
    // === 새로운 API용 메서드들 ===
    
    // 모든 관리자 조회 (응답용)
    public List<AdminResponseDto> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(AdminResponseDto::new)
                .collect(Collectors.toList());
    }
    
    // ID로 관리자 조회 (응답용)
    public Optional<AdminResponseDto> getAdminById(Long id) {
        return adminRepository.findById(id)
                .map(AdminResponseDto::new);
    }
    
    // 새 관리자 생성
    public AdminResponseDto createAdmin(AdminCreateRequestDto requestDto) {
        // 사용자명 중복 확인
        if (existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }
        
        Admin admin = new Admin();
        admin.setUsername(requestDto.getUsername());
        admin.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        admin.setName(requestDto.getName());
        admin.setEmail(requestDto.getEmail());
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        
        Admin savedAdmin = adminRepository.save(admin);
        return new AdminResponseDto(savedAdmin);
    }
    
    // 관리자 정보 업데이트
    public AdminResponseDto updateAdmin(Long id, AdminUpdateRequestDto requestDto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
        
        if (requestDto.getUsername() != null) {
            // 사용자명 변경 시 중복 확인
            if (!requestDto.getUsername().equals(admin.getUsername()) && 
                existsByUsername(requestDto.getUsername())) {
                throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
            }
            admin.setUsername(requestDto.getUsername());
        }
        
        if (requestDto.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        
        if (requestDto.getName() != null) {
            admin.setName(requestDto.getName());
        }
        
        if (requestDto.getEmail() != null) {
            admin.setEmail(requestDto.getEmail());
        }
        
        if (requestDto.getActive() != null) {
            admin.setActive(requestDto.getActive());
        }
        
        admin.setUpdatedAt(LocalDateTime.now());
        
        Admin updatedAdmin = adminRepository.save(admin);
        return new AdminResponseDto(updatedAdmin);
    }
    
    // 관리자 삭제 (비활성화)
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
        
        admin.setActive(false);
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
    }
    
    // 사용자명 존재 여부 확인
    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }
} 