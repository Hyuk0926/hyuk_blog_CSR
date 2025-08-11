package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.CommentDto;
import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.service.CommentService;
import com.example.hyuk_blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    /**
     * 댓글 목록 조회 API
     * GET /api/posts/{postId}/comments
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "KR") String postTypeStr) {

        System.out.println("=== GET COMMENTS METHOD ENTERED ===");
        System.out.println("PostId: " + postId);
        System.out.println("PostType: " + postTypeStr);

        try {
            // 수정된 서비스 메서드 호출
            List<CommentDto> comments = commentService.getCommentsByPost(postId, postTypeStr);
            System.out.println("Comments found: " + comments.size());
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            System.out.println("ERROR in getComments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    /**
     * 댓글 생성 API
     * POST /api/posts/{postId}/comments
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "KR") String postTypeStr,
            @RequestBody CommentDto commentDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        System.out.println("=== COMMENT CREATE METHOD ENTERED ===");
        System.out.println("PostId: " + postId);
        System.out.println("PostType: " + postTypeStr);
        System.out.println("CommentDto: " + commentDto);
        System.out.println("UserDetails: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        if (commentDto.getContent() == null || commentDto.getContent().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "댓글 내용을 입력해주세요."));
        }

        try {
            // userDetails에서 username을 가져와 User 엔티티를 조회합니다.
            User user = userService.findUserEntityByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            CommentDto createdComment = commentService.createComment(
                postId, 
                postTypeStr, 
                commentDto.getContent().trim(), 
                user.getId(), 
                user.getNickname()
            );

            return ResponseEntity.ok(createdComment);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("PostKr not found") || errorMessage.contains("PostJp not found")) {
                return ResponseEntity.status(404).body(Map.of("error", "게시글을 찾을 수 없습니다. (ID: " + postId + ", Type: " + postTypeStr + ")"));
            } else if (errorMessage.contains("User not found")) {
                return ResponseEntity.status(404).body(Map.of("error", "사용자 정보를 찾을 수 없습니다."));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", errorMessage));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "댓글 작성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 댓글 삭제 API
     * DELETE /api/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            // userDetails에서 username을 가져와 User 엔티티를 조회합니다.
            User user = userService.findUserEntityByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            boolean deleted = commentService.deleteComment(commentId, user.getId());

            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "댓글이 삭제되었습니다."));
            } else {
                return ResponseEntity.status(403).body(Map.of("error", "댓글을 삭제할 권한이 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "댓글 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
