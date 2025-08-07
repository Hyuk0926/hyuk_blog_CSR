package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.ApiResponseDto;
import com.example.hyuk_blog.dto.resume.ResumeResponseDto;
import com.example.hyuk_blog.dto.resume.ResumeUpdateRequestDto;
import com.example.hyuk_blog.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeApiController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 언어별 이력서 조회
     * GET /api/resume?lang=ko 또는 GET /api/resume?lang=ja
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<ResumeResponseDto>> getResume(
            @RequestParam(value = "lang", defaultValue = "ko") String lang) {
        try {
            ResumeResponseDto resume = resumeService.getResumeByLang(lang);
            return ResponseEntity.ok(ApiResponseDto.success(resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("이력서 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 이력서 수정 (관리자용)
     * PUT /api/resume
     */
    @PutMapping
    public ResponseEntity<ApiResponseDto<ResumeResponseDto>> updateResume(
            @RequestBody ResumeUpdateRequestDto requestDto) {
        try {
            ResumeResponseDto updatedResume = resumeService.updateResume(requestDto);
            return ResponseEntity.ok(ApiResponseDto.success(updatedResume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("이력서 수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 