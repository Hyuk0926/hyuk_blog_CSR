package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.InquiryDto;
import com.example.hyuk_blog.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContactApiController {

    private final InquiryService inquiryService;

    /**
     * 문의하기 API
     * POST /api/contact
     * 
     * @param inquiry 문의 정보
     * @return 문의 처리 결과
     */
    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> submitContact(@RequestBody InquiryDto inquiry) {
        try {
            log.info("Contact submission received - Name: {}, Email: {}, Subject: {}", 
                    inquiry.getName(), inquiry.getEmail(), inquiry.getSubject());
            
            // 필수 필드 검증
            if (inquiry.getName() == null || inquiry.getName().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "이름은 필수 입력 항목입니다.");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (inquiry.getEmail() == null || inquiry.getEmail().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "이메일은 필수 입력 항목입니다.");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (inquiry.getSubject() == null || inquiry.getSubject().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "제목은 필수 입력 항목입니다.");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (inquiry.getMessage() == null || inquiry.getMessage().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "문의 내용은 필수 입력 항목입니다.");
                return ResponseEntity.badRequest().body(error);
            }
            
            // 문의 저장
            inquiryService.addInquiry(inquiry);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "문의가 성공적으로 접수되었습니다. 빠른 시일 내에 답변드리겠습니다.");
            
            log.info("Contact submission successful for: {}", inquiry.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to submit contact: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "문의 접수 중 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
