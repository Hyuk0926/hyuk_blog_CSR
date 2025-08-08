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
            @RequestParam(defaultValue = "KR") PostType postType) {
        
        try {
            List<CommentDto> comments;
            if (postType == PostType.KR) {
                comments = commentService.getCommentsByPostKrId(postId);
            } else {
                comments = commentService.getCommentsByPostJpId(postId);
            }
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
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
            @RequestParam(defaultValue = "KR") PostType postType,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "댓글 내용을 입력해주세요."));
        }

        try {
            Optional<UserDto> userOpt = userService.findByUsername(userDetails.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "사용자 정보를 찾을 수 없습니다."));
            }

            UserDto userDto = userOpt.get();
            CommentDto createdComment = commentService.createComment(
                postId, 
                postType, 
                content.trim(), 
                userDto.getId(), 
                userDto.getNickname()
            );

            return ResponseEntity.ok(createdComment);
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
            Optional<UserDto> userOpt = userService.findByUsername(userDetails.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "사용자 정보를 찾을 수 없습니다."));
            }

            UserDto userDto = userOpt.get();
            boolean deleted = commentService.deleteComment(commentId, userDto.getId());

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
