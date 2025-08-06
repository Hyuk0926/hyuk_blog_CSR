package com.example.hyuk_blog.dto.post;

import com.example.hyuk_blog.entity.Category;
import com.example.hyuk_blog.entity.PostCommon;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDto {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private boolean published;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
    private Long commentCount;
    private PostType postType; // PostType enum 사용
    private boolean isLiked; // 현재 사용자가 좋아요를 눌렀는지 여부
    private List<CommentDto> comments; // 댓글 목록
    private boolean isLoggedIn; // 로그인 상태
    private boolean isAdmin; // 관리자 여부

    /**
     * PostCommon 인터페이스를 구현하는 엔티티로부터 PostDetailResponseDto 생성
     */
    public static PostDetailResponseDto fromPostCommon(PostCommon post) {
        PostDetailResponseDto dto = new PostDetailResponseDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSummary(post.getSummary());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setPublished(post.isPublished());
        dto.setCategory(post.getCategory());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setPostType(post.getPostType());
        return dto;
    }

    /**
     * 언어 코드 반환 (PostType 기반)
     */
    public String getLanguage() {
        return postType == PostType.JP ? "ja" : "ko";
    }
} 