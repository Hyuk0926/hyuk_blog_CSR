package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.ApiResponseDto;
import com.example.hyuk_blog.dto.post.*;
import com.example.hyuk_blog.entity.Category;
import com.example.hyuk_blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostApiController {

    @Autowired
    private PostService postService;

    // ==================== 조회 API 엔드포인트 ====================

    /**
     * 모든 공개 게시글 목록 조회
     * GET /api/posts?lang=ko
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getAllPosts(
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            List<PostResponseDto> posts = postService.findAll(lang);
            return ResponseEntity.ok(ApiResponseDto.success(posts, "게시글 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("게시글 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 특정 게시글 상세 조회
     * GET /api/posts/{id}?lang=ko
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> getPostById(
            @PathVariable Long id,
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            Optional<PostResponseDto> post = postService.findById(id, lang);
            if (post.isPresent()) {
                return ResponseEntity.ok(ApiResponseDto.success(post.get(), "게시글을 성공적으로 조회했습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("해당 ID의 게시글을 찾을 수 없습니다.", "POST_NOT_FOUND"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("게시글 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 제목으로 게시글 검색
     * GET /api/posts/search?query=검색어&lang=ko
     */
    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> searchPosts(
            @RequestParam String query,
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            List<PostResponseDto> posts = postService.searchByTitle(query, lang);
            return ResponseEntity.ok(ApiResponseDto.success(posts, "검색 결과를 성공적으로 조회했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("게시글 검색 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 카테고리별 게시글 조회
     * GET /api/posts/category/{category}?lang=ko
     */
    @GetMapping("/posts/category/{category}")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostsByCategory(
            @PathVariable Category category,
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            List<PostResponseDto> posts = postService.findByCategory(category, lang);
            return ResponseEntity.ok(ApiResponseDto.success(posts, "카테고리별 게시글을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("카테고리별 게시글 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 모든 게시글 조회 (관리자용)
     * GET /api/posts/admin?lang=ko
     */
    @GetMapping("/posts/admin")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getAllPostsForAdmin(
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            List<PostResponseDto> posts = postService.findAllForAdmin(lang);
            return ResponseEntity.ok(ApiResponseDto.success(posts, "모든 게시글을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("관리자용 게시글 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ==================== 생성 API 엔드포인트 ====================

    /**
     * 새 게시글 작성
     * POST /api/posts?lang=ko
     */
    @PostMapping("/posts")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> createPost(
            @Valid @RequestBody PostCreateRequestDto requestDto,
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            PostResponseDto createdPost = postService.save(requestDto, lang);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(createdPost, "게시글이 성공적으로 생성되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("잘못된 요청입니다: " + e.getMessage(), "INVALID_REQUEST"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("게시글 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ==================== 수정 API 엔드포인트 ====================

    /**
     * 게시글 수정
     * PUT /api/posts/{id}?lang=ko
     */
    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequestDto requestDto,
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            Optional<PostResponseDto> updatedPost = postService.update(id, requestDto, lang);
            if (updatedPost.isPresent()) {
                return ResponseEntity.ok(ApiResponseDto.success(updatedPost.get(), "게시글이 성공적으로 수정되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("해당 ID의 게시글을 찾을 수 없습니다.", "POST_NOT_FOUND"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("잘못된 요청입니다: " + e.getMessage(), "INVALID_REQUEST"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("게시글 수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ==================== 삭제 API 엔드포인트 ====================

    /**
     * 게시글 삭제
     * DELETE /api/posts/{id}?lang=ko
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deletePost(
            @PathVariable Long id,
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            boolean deleted = postService.deleteById(id, lang);
            if (deleted) {
                return ResponseEntity.ok(ApiResponseDto.success(null, "게시글이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("해당 ID의 게시글을 찾을 수 없습니다.", "POST_NOT_FOUND"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("게시글 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ==================== 추가 유틸리티 API 엔드포인트 ====================

    /**
     * 사용 가능한 카테고리 목록 조회
     * GET /api/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponseDto<Category[]>> getCategories() {
        try {
            Category[] categories = Category.values();
            return ResponseEntity.ok(ApiResponseDto.success(categories, "카테고리 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("카테고리 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 게시글 존재 여부 확인
     * GET /api/posts/{id}/exists?lang=ko
     */
    @GetMapping("/posts/{id}/exists")
    public ResponseEntity<ApiResponseDto<Boolean>> checkPostExists(
            @PathVariable Long id,
            @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        try {
            Optional<PostResponseDto> post = postService.findById(id, lang);
            boolean exists = post.isPresent();
            return ResponseEntity.ok(ApiResponseDto.success(exists, "게시글 존재 여부를 확인했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("게시글 존재 여부 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ==================== 에러 핸들링 ====================

    /**
     * 잘못된 요청 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error("잘못된 요청입니다: " + e.getMessage(), "INVALID_REQUEST"));
    }

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error("서버 내부 오류가 발생했습니다: " + e.getMessage(), "INTERNAL_ERROR"));
    }
} 