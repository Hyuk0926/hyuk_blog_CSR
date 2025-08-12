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
        
        System.out.println("=== POST DETAIL API DEBUG ===");
        System.out.println("[PostApiController] getPostById called with id: " + id + ", lang: " + lang);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<PostDto> post = postService.getPostById(id, lang);
            
            if (post.isPresent()) {
                PostDto postData = post.get();
                System.out.println("[PostApiController] Post found - ID: " + postData.getId() + 
                                 ", titleKo: " + (postData.getTitleKo() != null ? "있음" : "없음") + 
                                 ", titleJa: " + (postData.getTitleJa() != null ? "있음" : "없음") +
                                 ", lang: " + postData.getLang());
                
                // 언어별 제목, 요약, 내용 설정
                String title = postData.getTitle(lang);
                String summary = postData.getSummary(lang);
                String content = postData.getContent(lang);
                
                System.out.println("[PostApiController] Processed data - title: " + title + 
                                 ", summary: " + (summary != null ? "있음" : "없음") + 
                                 ", content: " + (content != null ? "있음" : "없음"));
                
                response.put("success", true);
                response.put("data", postData);
                response.put("message", "게시글을 성공적으로 조회했습니다.");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("[PostApiController] Post not found for ID: " + id + ", lang: " + lang);
                response.put("success", false);
                response.put("message", "게시글을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            System.err.println("[PostApiController] Exception occurred: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
        
        System.out.println("=== DELETE POST API DEBUG ===");
        System.out.println("[PostApiController] deletePost called with id: " + id + ", lang: " + lang);
        
        boolean deleted = postService.deletePost(id, lang);
        
        Map<String, Object> response = new HashMap<>();
        
        if (deleted) {
            System.out.println("[PostApiController] Post deleted successfully - ID: " + id);
            response.put("success", true);
            response.put("message", "게시글이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("[PostApiController] Post not found for deletion - ID: " + id + ", lang: " + lang);
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
        
        System.out.println("=== POST API DEBUG ===");
        System.out.println("[PostApiController] getAdminPosts called with lang: " + lang);
        System.out.println("[PostApiController] Request received at: " + java.time.LocalDateTime.now());
        
        List<PostDto> posts = postService.getAllPosts(lang);
        System.out.println("[PostApiController] Retrieved " + posts.size() + " posts for language: " + lang);
        
        // 각 게시글의 언어 정보 출력
        for (int i = 0; i < Math.min(posts.size(), 5); i++) { // 처음 5개만 출력
            PostDto post = posts.get(i);
            System.out.println("Post " + (i+1) + ": ID=" + post.getId() + 
                             ", titleKo=" + (post.getTitleKo() != null ? "있음" : "없음") + 
                             ", titleJa=" + (post.getTitleJa() != null ? "있음" : "없음"));
        }
        
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
