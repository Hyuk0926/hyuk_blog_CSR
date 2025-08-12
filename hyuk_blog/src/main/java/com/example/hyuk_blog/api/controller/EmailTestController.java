package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    /**
     * 이메일 발송 테스트 API
     * POST /api/admin/email/test
     */
    @PostMapping("/email/test")
    public ResponseEntity<Map<String, Object>> testEmail(@RequestBody Map<String, String> request) {
        try {
            String toEmail = request.get("toEmail");
            
            if (toEmail == null || toEmail.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "수신자 이메일을 입력해주세요.");
                return ResponseEntity.badRequest().body(error);
            }
            
            emailService.sendTestEmail(toEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "테스트 이메일이 성공적으로 발송되었습니다.");
            
            log.info("테스트 이메일 발송 성공: {}", toEmail);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("테스트 이메일 발송 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "이메일 발송에 실패했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
