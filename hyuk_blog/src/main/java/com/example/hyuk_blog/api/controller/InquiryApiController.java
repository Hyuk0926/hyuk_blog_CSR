package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.InquiryDto;
import com.example.hyuk_blog.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class InquiryApiController {

    private final InquiryService inquiryService;

    /**
     * 문의 목록 조회 API
     * GET /api/admin/inquiries
     */
    @GetMapping("/inquiries")
    public ResponseEntity<Map<String, Object>> getInquiries() {
        try {
            log.info("문의 목록 조회 요청");
            List<InquiryDto> inquiries = inquiryService.getAllInquiries();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", inquiries);
            response.put("message", "문의 목록을 성공적으로 조회했습니다.");
            
            log.info("문의 목록 조회 성공: {}개", inquiries.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("문의 목록 조회 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "문의 목록 조회에 실패했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 최근 문의 조회 API
     * GET /api/admin/inquiries/recent
     */
    @GetMapping("/inquiries/recent")
    public ResponseEntity<Map<String, Object>> getRecentInquiries(
            @RequestParam(defaultValue = "5") int count) {
        try {
            log.info("최근 문의 조회 요청: {}개", count);
            List<InquiryDto> inquiries = inquiryService.getRecentInquiries(count);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", inquiries);
            response.put("message", "최근 문의를 성공적으로 조회했습니다.");
            
            log.info("최근 문의 조회 성공: {}개", inquiries.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("최근 문의 조회 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "최근 문의 조회에 실패했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 문의 읽음 처리 API
     * POST /api/admin/inquiries/read
     */
    @PostMapping("/inquiries/read")
    public ResponseEntity<Map<String, Object>> markInquiriesRead() {
        try {
            log.info("문의 읽음 처리 요청");
            inquiryService.markAllRead();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "모든 문의가 읽음 처리되었습니다.");
            
            log.info("문의 읽음 처리 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("문의 읽음 처리 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "문의 읽음 처리에 실패했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 문의 삭제 API
     * DELETE /api/admin/inquiries/{id}
     */
    @DeleteMapping("/inquiries/{id}")
    public ResponseEntity<Map<String, Object>> deleteInquiry(@PathVariable Long id) {
        try {
            log.info("문의 삭제 요청: ID {}", id);
            
            boolean deleted = inquiryService.deleteInquiry(id);
            
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "문의가 성공적으로 삭제되었습니다.");
                
                log.info("문의 삭제 성공: ID {}", id);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "문의를 찾을 수 없습니다.");
                
                log.warn("문의 삭제 실패: ID {} - 문의를 찾을 수 없음", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("문의 삭제 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "문의 삭제에 실패했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 문의 답변 API
     * POST /api/admin/inquiries/{id}/reply
     */
    @PostMapping("/inquiries/{id}/reply")
    public ResponseEntity<Map<String, Object>> replyInquiry(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            log.info("문의 답변 요청: ID {}", id);
            
            String replyMessage = request.get("replyMessage");
            String repliedBy = request.get("repliedBy");
            
            if (replyMessage == null || replyMessage.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "답변 내용을 입력해주세요.");
                return ResponseEntity.badRequest().body(error);
            }
            
            boolean replied = inquiryService.replyToInquiry(id, replyMessage, repliedBy);
            
            if (replied) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "답변이 성공적으로 등록되었습니다.");
                
                log.info("문의 답변 성공: ID {}", id);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "문의를 찾을 수 없습니다.");
                
                log.warn("문의 답변 실패: ID {} - 문의를 찾을 수 없음", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("문의 답변 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "답변 등록에 실패했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
