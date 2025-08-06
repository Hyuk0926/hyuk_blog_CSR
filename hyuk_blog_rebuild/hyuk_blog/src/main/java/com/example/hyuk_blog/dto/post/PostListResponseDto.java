package com.example.hyuk_blog.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
    private List<PostResponseDto> posts;
    private int totalCount;
    private String language;
    private String category; // 선택적 카테고리 필터
    private String searchQuery; // 검색어가 있는 경우
} 