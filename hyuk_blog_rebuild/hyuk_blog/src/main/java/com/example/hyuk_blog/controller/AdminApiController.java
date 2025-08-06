package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.ApiResponseDto;
import com.example.hyuk_blog.dto.admin.AdminCreateRequestDto;
import com.example.hyuk_blog.dto.admin.AdminResponseDto;
import com.example.hyuk_blog.dto.admin.AdminUpdateRequestDto;
import com.example.hyuk_blog.service.AdminService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    private AdminService adminService;

    // 모든 관리자 조회
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<AdminResponseDto>>> getAllAdmins() {
        try {
            List<AdminResponseDto> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(ApiResponseDto.success(admins, "관리자 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("관리자 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 특정 관리자 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AdminResponseDto>> getAdminById(@PathVariable Long id) {
        try {
            return adminService.getAdminById(id)
                    .map(admin -> ResponseEntity.ok(ApiResponseDto.success(admin, "관리자 정보를 성공적으로 조회했습니다.")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponseDto.error("관리자를 찾을 수 없습니다.")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("관리자 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 새 관리자 생성
    @PostMapping
    public ResponseEntity<ApiResponseDto<AdminResponseDto>> createAdmin(@Valid @RequestBody AdminCreateRequestDto requestDto) {
        try {
            AdminResponseDto createdAdmin = adminService.createAdmin(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(createdAdmin, "관리자가 성공적으로 생성되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("관리자 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 관리자 정보 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AdminResponseDto>> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateRequestDto requestDto) {
        try {
            AdminResponseDto updatedAdmin = adminService.updateAdmin(id, requestDto);
            return ResponseEntity.ok(ApiResponseDto.success(updatedAdmin, "관리자 정보가 성공적으로 업데이트되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("관리자 업데이트 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 관리자 삭제 (비활성화)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok(ApiResponseDto.success(null, "관리자가 성공적으로 비활성화되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("관리자 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 사용자명 중복 확인
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponseDto<Boolean>> checkUsernameAvailability(@RequestParam String username) {
        try {
            boolean isAvailable = !adminService.existsByUsername(username);
            return ResponseEntity.ok(ApiResponseDto.success(isAvailable, 
                    isAvailable ? "사용 가능한 사용자명입니다." : "이미 사용 중인 사용자명입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("사용자명 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 현재 로그인한 관리자 프로필 조회
    @GetMapping("/auth/profile")
    public ResponseEntity<ApiResponseDto<AdminResponseDto>> getCurrentAdminProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("인증 토큰이 필요합니다."));
            }
            
            String token = authHeader.substring(7); // "Bearer " 제거
            
            // JWT 토큰에서 사용자명 추출 (간단한 구현)
            // 실제로는 JwtUtil을 사용하여 토큰을 파싱해야 함
            String username = extractUsernameFromToken(token);
            
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("유효하지 않은 토큰입니다."));
            }
            
            // 사용자명으로 관리자 조회
            return adminService.findByUsername(username)
                    .map(adminDto -> {
                        AdminResponseDto responseDto = new AdminResponseDto(adminDto.toEntity());
                        return ResponseEntity.ok(ApiResponseDto.success(responseDto, "관리자 프로필을 성공적으로 조회했습니다."));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponseDto.error("관리자를 찾을 수 없습니다.")));
                            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("관리자 프로필 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    // JWT 토큰에서 사용자명 추출 (임시 구현)
    private String extractUsernameFromToken(String token) {
        try {
            // JWT 토큰의 두 번째 부분(payload)을 디코딩
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            // Base64 디코딩 (패딩 추가)
            String payload = parts[1];
            String paddedPayload = payload + "=".repeat((4 - payload.length() % 4) % 4);
            String decodedPayload = new String(java.util.Base64.getDecoder().decode(paddedPayload));
            
            // JSON 파싱하여 sub 필드 추출
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(decodedPayload);
            return jsonNode.get("sub").asText();
            
        } catch (Exception e) {
            return null;
        }
    }
} 