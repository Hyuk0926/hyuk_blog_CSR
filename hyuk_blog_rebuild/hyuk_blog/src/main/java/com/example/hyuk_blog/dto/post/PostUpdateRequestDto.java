package com.example.hyuk_blog.dto.post;

import com.example.hyuk_blog.entity.Category;
import com.example.hyuk_blog.entity.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequestDto {
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    @NotBlank(message = "요약은 필수입니다")
    private String summary;
    
    @NotBlank(message = "내용은 필수입니다")
    private String content;
    
    private String imageUrl;
    
    @NotNull(message = "카테고리는 필수입니다")
    private Category category;
    
    private boolean published;
    
    @NotNull(message = "게시글 타입은 필수입니다")
    private PostType postType; // PostType enum 사용

    /**
     * 언어 코드 반환 (PostType 기반)
     */
    public String getLanguage() {
        return postType == PostType.JP ? "ja" : "ko";
    }
} 