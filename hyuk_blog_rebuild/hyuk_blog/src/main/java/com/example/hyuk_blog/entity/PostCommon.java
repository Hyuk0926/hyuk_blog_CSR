package com.example.hyuk_blog.entity;

import java.time.LocalDateTime;

/**
 * PostKr과 PostJp 엔티티의 공통 필드들을 정의하는 인터페이스
 * 다국어 게시글 엔티티들이 구현해야 하는 공통 계약을 정의
 */
public interface PostCommon {
    
    /**
     * 게시글 ID
     */
    Long getId();
    
    /**
     * 게시글 제목
     */
    String getTitle();
    
    /**
     * 게시글 요약
     */
    String getSummary();
    
    /**
     * 게시글 내용
     */
    String getContent();
    
    /**
     * 이미지 URL
     */
    String getImageUrl();
    
    /**
     * 공개 여부
     */
    boolean isPublished();
    
    /**
     * 카테고리
     */
    Category getCategory();
    
    /**
     * 생성일시
     */
    LocalDateTime getCreatedAt();
    
    /**
     * 수정일시
     */
    LocalDateTime getUpdatedAt();
    
    /**
     * 게시글 언어 타입 반환
     */
    PostType getPostType();
} 