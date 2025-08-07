package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.entity.Resume;
import com.example.hyuk_blog.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResumeApiController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 이력서 조회 API
     * GET /api/resume
     * 
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 해당 언어의 이력서 정보
     */
    @GetMapping("/resume")
    public ResponseEntity<Map<String, Object>> getResumeByLang(
            @RequestParam(defaultValue = "ko") String lang) {
        
        Resume resume = resumeService.loadResume(lang);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", resume);
        response.put("message", "이력서를 성공적으로 조회했습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 이력서 수정 API
     * PUT /api/resume
     * 
     * @param resume 수정할 이력서 정보
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 수정된 이력서 정보
     */
    @PutMapping("/resume")
    public ResponseEntity<Map<String, Object>> updateResume(
            @RequestBody Resume resume,
            @RequestParam(defaultValue = "ko") String lang) {
        
        try {
            resumeService.saveResume(resume, lang);
            
            // 저장된 이력서 다시 조회
            Resume savedResume = resumeService.loadResume(lang);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedResume);
            response.put("message", "이력서가 성공적으로 수정되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "이력서 수정 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
