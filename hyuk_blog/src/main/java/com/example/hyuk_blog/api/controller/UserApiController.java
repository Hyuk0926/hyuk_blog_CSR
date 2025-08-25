package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.service.UserService;
import com.example.hyuk_blog.service.JwtAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final JwtAuthService jwtAuthService;

    /**
     * 현재 로그인한 사용자 정보 조회
     * GET /api/user/info
     */
    @GetMapping("/info")
    public ResponseEntity<?> getCurrentUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // 인증이 없는 경우 401 Unauthorized 반환
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7); // "Bearer " 제거
            String username = jwtAuthService.getUsernameFromToken(token);
            String role = jwtAuthService.getRoleFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            // 사용자 정보 조회
            Optional<UserDto> userInfoOpt = userService.findByUsername(username);
            
            if (userInfoOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }
            
            UserDto userInfo = userInfoOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("username", userInfo.getUsername());
            response.put("nickname", userInfo.getNickname());
            response.put("email", userInfo.getEmail());
            response.put("profileImage", userInfo.getProfileImage());
            response.put("bio", userInfo.getBio());
            response.put("backgroundStyle", userInfo.getBackgroundStyle());
            response.put("backgroundColor", userInfo.getBackgroundColor());
            response.put("backgroundImage", userInfo.getBackgroundImage());
            response.put("role", role);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get user info: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user info");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 사용자명 중복 확인 API
     * GET /api/user/check-username
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            boolean available = userService.isUsernameAvailable(username);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("username", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Username check error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "사용자명 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 닉네임 중복 확인 API
     * GET /api/user/check-nickname
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        try {
            boolean available = userService.isNicknameAvailable(nickname);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("nickname", nickname);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Nickname check error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "닉네임 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 이메일 중복 확인 API
     * GET /api/user/check-email
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            boolean available = userService.isEmailAvailable(email);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("email", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Email check error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "이메일 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 프로필 이미지 업로드 API
     * POST /api/user/profile-image
     */
    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("image") MultipartFile image,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            String username = jwtAuthService.getUsernameFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            // 파일 검증
            if (image.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "이미지 파일이 선택되지 않았습니다.");
                return ResponseEntity.badRequest().body(error);
            }

            // 파일 크기 검증 (5MB 제한)
            if (image.getSize() > 5 * 1024 * 1024) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "이미지 파일 크기는 5MB 이하여야 합니다.");
                return ResponseEntity.badRequest().body(error);
            }

            // 파일 타입 검증
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "이미지 파일만 업로드 가능합니다.");
                return ResponseEntity.badRequest().body(error);
            }

            // 업로드 디렉토리 생성
            String uploadDir = "uploads/profile-images";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 원본 파일명과 확장자 추출
            String originalFilename = image.getOriginalFilename();
            String extension = "";
            String baseName = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            }
            
            // UUID 파일명 생성 (백업용)
            String uuidFilename = UUID.randomUUID().toString() + extension;
            
            // 원본 파일명으로 저장 (중복 시 처리)
            String finalOriginalFilename = originalFilename;
            File originalFile = new File(uploadDir, finalOriginalFilename);
            int counter = 1;
            while (originalFile.exists()) {
                finalOriginalFilename = baseName + "_" + counter + extension;
                originalFile = new File(uploadDir, finalOriginalFilename);
                counter++;
            }
            
            // 원본 이름으로 파일 저장
            Path originalFilePath = Paths.get(uploadDir, finalOriginalFilename);
            Files.copy(image.getInputStream(), originalFilePath);
            
            // UUID 이름으로 백업 파일 저장
            Path backupFilePath = Paths.get(uploadDir, uuidFilename);
            Files.copy(image.getInputStream(), backupFilePath);

            // 사용자 정보 업데이트 (원본 이름 사용)
            String imageUrl = "/uploads/profile-images/" + finalOriginalFilename;
            userService.updateProfileImage(username, imageUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("profileImage", imageUrl);
            response.put("originalFilename", finalOriginalFilename);
            response.put("backupFilename", uuidFilename);
            response.put("message", "프로필 이미지가 성공적으로 업로드되었습니다.");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Profile image upload error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        } catch (Exception e) {
            log.error("Profile image upload error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "프로필 이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 사용자 프로필 수정 API
     * PUT /api/user/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> profileData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            String username = jwtAuthService.getUsernameFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            String nickname = profileData.get("nickname");
            String email = profileData.get("email");
            String bio = profileData.get("bio");

            // 사용자 정보 업데이트
            UserDto updatedUser = userService.updateProfile(username, nickname, email, bio);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "프로필이 성공적으로 수정되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Profile update error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 비밀번호 변경 API
     * PUT /api/user/password
     */
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> passwordData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            String username = jwtAuthService.getUsernameFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            // 비밀번호 변경
            userService.changePassword(username, currentPassword, newPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "비밀번호가 성공적으로 변경되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Password change error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 배경 이미지 업로드 API
     * POST /api/user/background-image
     */
    @PostMapping("/background-image")
    public ResponseEntity<?> uploadBackgroundImage(
            @RequestParam("image") MultipartFile image,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            String username = jwtAuthService.getUsernameFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            // 파일 검증
            if (image.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "이미지 파일이 선택되지 않았습니다.");
                return ResponseEntity.badRequest().body(error);
            }

            // 파일 크기 검증 (10MB 제한)
            if (image.getSize() > 10 * 1024 * 1024) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "이미지 파일 크기는 10MB 이하여야 합니다.");
                return ResponseEntity.badRequest().body(error);
            }

            // 파일 타입 검증
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "이미지 파일만 업로드 가능합니다.");
                return ResponseEntity.badRequest().body(error);
            }

            // 업로드 디렉토리 생성
            String uploadDir = "uploads/background-images";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 원본 파일명과 확장자 추출
            String originalFilename = image.getOriginalFilename();
            String extension = "";
            String baseName = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            }
            
            // UUID 파일명 생성
            String uuidFilename = UUID.randomUUID().toString() + extension;
            
            // 원본 파일명으로 저장 (중복 시 처리)
            String finalOriginalFilename = originalFilename;
            File originalFile = new File(uploadDir, finalOriginalFilename);
            int counter = 1;
            while (originalFile.exists()) {
                finalOriginalFilename = baseName + "_" + counter + extension;
                originalFile = new File(uploadDir, finalOriginalFilename);
                counter++;
            }
            
            // 파일 저장
            Path filePath = Paths.get(uploadDir, finalOriginalFilename);
            Files.copy(image.getInputStream(), filePath);

            // 이미지 URL 생성
            String imageUrl = "/uploads/background-images/" + finalOriginalFilename;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("backgroundImage", imageUrl);
            response.put("originalFilename", finalOriginalFilename);
            response.put("message", "배경 이미지가 성공적으로 업로드되었습니다.");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Background image upload error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        } catch (Exception e) {
            log.error("Background image upload error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "배경 이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 배경화면 커스터마이징 API
     * PUT /api/user/background
     */
    @PutMapping("/background")
    public ResponseEntity<?> updateBackground(
            @RequestBody Map<String, String> backgroundData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            String username = jwtAuthService.getUsernameFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            String backgroundStyle = backgroundData.get("backgroundStyle");
            String backgroundColor = backgroundData.get("backgroundColor");
            String backgroundImage = backgroundData.get("backgroundImage");

            // 배경화면 설정 업데이트
            UserDto updatedUser = userService.updateBackground(username, backgroundStyle, backgroundColor, backgroundImage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "배경화면이 성공적으로 저장되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Background update error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "배경화면 저장 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 계정 삭제 API
     * DELETE /api/user/account
     */
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            String username = jwtAuthService.getUsernameFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            // 계정 삭제
            userService.deleteAccount(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "계정이 성공적으로 삭제되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Account deletion error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "계정 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
