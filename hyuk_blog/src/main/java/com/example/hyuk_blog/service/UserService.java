package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.dto.AdminDto;
import com.example.hyuk_blog.dto.UserRegistrationDto;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 사용자 로그인 인증 (admin 계정도 포함)
    public Optional<UserDto> authenticate(String username, String password) {
        // 1. User 테이블에서 사용자 찾기
        Optional<UserDto> user = userRepository.findByUsernameAndActiveTrue(username)
                .filter(u -> {
                    // BCrypt로 비밀번호 검증 시도
                    if (passwordEncoder.matches(password, u.getPassword())) {
                        return true;
                    }
                    
                    // BCrypt 실패 시 SHA-256으로 검증 시도 (기존 사용자 호환성)
                    String sha256Hash = hashPassword(password);
                    return u.getPassword().equals(sha256Hash);
                })
                .map(UserDto::fromEntity);
        
        if (user.isPresent()) {
            return user;
        }
        
        // 2. User 테이블에 없으면 Admin 테이블에서 확인
        Optional<AdminDto> admin = adminService.authenticate(username, password);
        if (admin.isPresent()) {
            // Admin을 UserDto로 변환하여 반환
            UserDto adminAsUser = new UserDto();
            adminAsUser.setId(admin.get().getId());
            adminAsUser.setUsername(admin.get().getUsername());
            adminAsUser.setPassword(admin.get().getPassword());
            adminAsUser.setNickname(admin.get().getUsername()); // admin username을 nickname으로 사용
            adminAsUser.setEmail(admin.get().getEmail());
            adminAsUser.setActive(admin.get().isActive());
            adminAsUser.setCreatedAt(admin.get().getCreatedAt());
            adminAsUser.setUpdatedAt(admin.get().getUpdatedAt());
            return Optional.of(adminAsUser);
        }
        
        return Optional.empty();
    }
    
    // SHA-256 해싱 (기존 사용자 호환성을 위해 유지)
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    // 사용자명으로 사용자 조회 (DTO 반환)
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDto::fromEntity);
    }
    
    // 사용자명으로 사용자 엔티티 조회
    public Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // 회원가입 (DTO 사용)
    @Transactional
    public UserDto register(com.example.hyuk_blog.dto.UserRegistrationDto registrationDto) {
        // 중복 검사
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }
        if (userRepository.existsByNickname(registrationDto.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
        if (registrationDto.getEmail() != null && !registrationDto.getEmail().isEmpty() 
            && userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword())); // BCrypt로 비밀번호 인코딩
        user.setNickname(registrationDto.getNickname());
        user.setEmail(registrationDto.getEmail());
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }
    
    // 회원가입 (기존 방식 - 호환성 유지)
    @Transactional
    public UserDto register(String username, String password, String nickname, String email) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setNickname(nickname);
        dto.setEmail(email);
        return register(dto);
    }
    
    // 사용자명 존재 여부 확인
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    // 닉네임 존재 여부 확인
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
    
    // 이메일 존재 여부 확인
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 프로필 이미지 업데이트
     */
    @Transactional
    public void updateProfileImage(String username, String imageUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        user.setProfileImage(imageUrl);
        userRepository.save(user);
    }

    /**
     * 프로필 정보 업데이트
     */
    @Transactional
    public UserDto updateProfile(String username, String nickname, String email, String bio) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 닉네임 중복 검사 (자신의 닉네임은 제외)
        if (nickname != null && !nickname.equals(user.getNickname()) && 
            userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
        
        // 이메일 중복 검사 (자신의 이메일은 제외)
        if (email != null && !email.equals(user.getEmail()) && 
            userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    /**
     * 배경화면 설정 업데이트
     */
    @Transactional
    public UserDto updateBackground(String username, String backgroundStyle, String backgroundColor, String backgroundImage) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (backgroundStyle != null) {
            user.setBackgroundStyle(backgroundStyle);
        }
        if (backgroundColor != null) {
            user.setBackgroundColor(backgroundColor);
        }
        if (backgroundImage != null) {
            user.setBackgroundImage(backgroundImage);
        }
        
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 현재 비밀번호 검증
        boolean currentPasswordValid = false;
        
        // BCrypt로 검증 시도
        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            currentPasswordValid = true;
        } else {
            // BCrypt 실패 시 SHA-256으로 검증 시도
            String sha256Hash = hashPassword(currentPassword);
            if (user.getPassword().equals(sha256Hash)) {
                currentPasswordValid = true;
            }
        }
        
        if (!currentPasswordValid) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        // 새 비밀번호로 업데이트 (BCrypt 사용)
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 계정 삭제
     */
    @Transactional
    public void deleteAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        user.setActive(false);
        userRepository.save(user);
    }
    
    // 사용자명 사용 가능 여부 확인
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    // 닉네임 사용 가능 여부 확인
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }
    
    // 이메일 사용 가능 여부 확인
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
} 