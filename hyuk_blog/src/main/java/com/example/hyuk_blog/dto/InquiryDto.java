package com.example.hyuk_blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {
    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
    private boolean replied = false;
    
    // 답변 관련 필드 추가
    private String replyMessage;
    private LocalDateTime repliedAt;
    private String repliedBy; // 답변한 관리자 이름
} 