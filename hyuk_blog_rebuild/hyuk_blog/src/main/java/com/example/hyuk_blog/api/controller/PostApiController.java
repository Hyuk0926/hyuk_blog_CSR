package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.PostDto;
import com.example.hyuk_blog.entity.Category;
import com.example.hyuk_blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostApiController {

    @Autowired
    private PostService postService;

    /**
     * 게시글 목록 조회 API
     * GET /api/posts
     * 
     * @param category 카테고리 필터 (선택사항)
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 게시글 목록
     */
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "ko") String lang) {
        
        List<PostDto> posts;
        
        if (category != null && !category.isEmpty()) {
            try {
                // 문자열을 Category enum으로 변환
                Category categoryEnum = Category.valueOf(category.toUpperCase());
                // 카테고리별 게시글 조회
                posts = postService.getPublishedPostsByCategory(categoryEnum, lang);
            } catch (IllegalArgumentException e) {
                // 잘못된 카테고리인 경우 전체 게시글 조회
                posts = postService.getAllPublishedPosts(lang);
            }
        } else {
            // 전체 게시글 조회
            posts = postService.getAllPublishedPosts(lang);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", posts);
        response.put("message", "게시글 목록을 성공적으로 조회했습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회 API
     * GET /api/posts/{id}
     * 
     * @param id 게시글 ID
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 게시글 상세 정보
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "ko") String lang) {
        
        Optional<PostDto> post = postService.getPostById(id, lang);
        
        Map<String, Object> response = new HashMap<>();
        
        if (post.isPresent()) {
            response.put("success", true);
            response.put("data", post.get());
            response.put("message", "게시글을 성공적으로 조회했습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "게시글을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 게시글 생성 API
     * POST /api/posts
     * 
     * @param postDto 생성할 게시글 정보
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 생성된 게시글 정보
     */
    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestBody PostDto postDto,
            @RequestParam(defaultValue = "ko") String lang) {
        
        PostDto savedPost = postService.savePost(postDto, lang);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", savedPost);
        response.put("message", "게시글이 성공적으로 생성되었습니다.");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 수정 API
     * PUT /api/posts/{id}
     * 
     * @param id 게시글 ID
     * @param postDto 수정할 게시글 정보
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 수정된 게시글 정보
     */
    @PutMapping("/posts/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto,
            @RequestParam(defaultValue = "ko") String lang) {
        
        Optional<PostDto> updatedPost = postService.updatePost(id, postDto, lang);
        
        Map<String, Object> response = new HashMap<>();
        
        if (updatedPost.isPresent()) {
            response.put("success", true);
            response.put("data", updatedPost.get());
            response.put("message", "게시글이 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "게시글을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 게시글 삭제 API
     * DELETE /api/posts/{id}
     * 
     * @param id 게시글 ID
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "ko") String lang) {
        
        boolean deleted = postService.deletePost(id, lang);
        
        Map<String, Object> response = new HashMap<>();
        
        if (deleted) {
            response.put("success", true);
            response.put("message", "게시글이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "게시글을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 관리자용 게시글 목록 조회 API (공개/비공개 모두)
     * GET /api/posts/admin
     * 
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 모든 게시글 목록
     */
    @GetMapping("/posts/admin")
    public ResponseEntity<Map<String, Object>> getAdminPosts(
            @RequestParam(defaultValue = "ko") String lang) {
        
        List<PostDto> posts = postService.getAllPosts(lang);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", posts);
        response.put("message", "관리자용 게시글 목록을 성공적으로 조회했습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자 대시보드 통계 API
     * GET /api/posts/admin/stats
     * 
     * @param lang 언어 설정 (선택사항, 기본값 "ko")
     * @return 대시보드 통계 데이터
     */
    @GetMapping("/posts/admin/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats(
            @RequestParam(defaultValue = "ko") String lang) {
        
        Map<String, Object> stats = postService.getAdminStats(lang);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        response.put("message", "관리자 통계를 성공적으로 조회했습니다.");
        
        return ResponseEntity.ok(response);
    }
}
