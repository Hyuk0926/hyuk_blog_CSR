package com.example.hyuk_blog.dto.post;

import com.example.hyuk_blog.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchRequestDto {
    @NotBlank(message = "검색어는 필수입니다")
    private String query;
    
    private String language = "ko"; // 기본값은 한국어
    
    private Category category; // 선택적 카테고리 필터
    
    private boolean publishedOnly = true; // 기본적으로 공개된 게시글만 검색
} 