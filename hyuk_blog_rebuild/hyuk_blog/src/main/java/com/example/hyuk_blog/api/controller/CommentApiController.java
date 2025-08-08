package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.CommentDto;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.service.CommentService;
import com.example.hyuk_blog.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    /**
     * 특정 게시글의 댓글 목록 조회 API
     * GET /api/posts/{postId}/comments
     * 
     * @param postId 게시글 ID
     * @param postType 게시글 타입 ("kr" 또는 "jp")
     * @return 댓글 목록
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam String postType) {
        
        try {
            List<CommentDto> comments;
            
            if ("kr".equalsIgnoreCase(postType)) {
                comments = commentService.getCommentsByPostKrId(postId);
            } else if ("jp".equalsIgnoreCase(postType)) {
                comments = commentService.getCommentsByPostJpId(postId);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 댓글 생성 API
     * POST /api/posts/{postId}/comments
     * 
     * @param postId 게시글 ID
     * @param postType 게시글 타입 ("kr" 또는 "jp")
     * @param commentDto 댓글 정보
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 댓글 정보
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long postId,
            @RequestParam String postType,
            @RequestBody CommentDto commentDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // 인증 확인
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // UserDetails를 User 엔티티로 변환
            User user = null;
            if (userDetails instanceof User) {
                user = (User) userDetails;
            } else {
                // Admin 계정인 경우 처리
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // PostType enum 변환
            PostType postTypeEnum;
            if ("kr".equalsIgnoreCase(postType)) {
                postTypeEnum = PostType.KR;
            } else if ("jp".equalsIgnoreCase(postType)) {
                postTypeEnum = PostType.JP;
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            // 댓글 생성
            CommentDto createdComment = commentService.createComment(
                postId, 
                postTypeEnum, 
                commentDto.getContent(), 
                user.getId(), 
                user.getNickname()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 댓글 수정 API
     * PUT /api/comments/{commentId}
     * 
     * @param commentId 댓글 ID
     * @param commentDto 수정할 댓글 정보
     * @param userDetails 인증된 사용자 정보
     * @return 수정된 댓글 정보
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // 인증 확인
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // UserDetails를 User 엔티티로 변환
            User user = null;
            if (userDetails instanceof User) {
                user = (User) userDetails;
            } else {
                // Admin 계정인 경우 처리
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // 댓글 수정
            boolean updated = commentService.updateComment(commentId, user.getId(), commentDto.getContent());
            
            if (updated) {
                // 수정된 댓글 정보 조회
                List<CommentDto> comments = commentService.getCommentsByPostKrId(commentId); // 임시로 조회
                // 실제로는 commentId로 직접 조회하는 메서드가 필요
                return ResponseEntity.ok(commentDto);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 댓글 삭제 API
     * DELETE /api/comments/{commentId}
     * 
     * @param commentId 댓글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // 인증 확인
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // UserDetails를 User 엔티티로 변환
            User user = null;
            if (userDetails instanceof User) {
                user = (User) userDetails;
            } else {
                // Admin 계정인 경우 처리
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // 댓글 삭제
            boolean deleted = commentService.deleteComment(commentId, user.getId());
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
