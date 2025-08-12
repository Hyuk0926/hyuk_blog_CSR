package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.CommentDto;
import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.entity.Comment;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.repository.CommentRepository;
import com.example.hyuk_blog.service.CommentService;
import com.example.hyuk_blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentRepository commentRepository;

    /**
     * 댓글 목록 조회 API
     * GET /api/posts/{postId}/comments
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Long postId,
            @RequestParam("postType") String postTypeStr) {

        System.out.println("=== GET COMMENTS METHOD ENTERED ===");
        System.out.println("PostId: " + postId);
        System.out.println("PostType: " + postTypeStr);
        
        // 파라미터 검증
        if (postId == null) {
            System.out.println("ERROR: postId is null");
            return ResponseEntity.badRequest().body(List.of());
        }
        if (postTypeStr == null || postTypeStr.trim().isEmpty()) {
            System.out.println("ERROR: postType is null or empty");
            return ResponseEntity.badRequest().body(List.of());
        }

        try {
            System.out.println("Calling commentService.getCommentsByPost...");
            // 수정된 서비스 메서드 호출
            List<CommentDto> comments = commentService.getCommentsByPost(postId, postTypeStr);
            System.out.println("Comments found: " + comments.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", comments);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid parameter in getComments: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "잘못된 파라미터입니다.");
            response.put("data", List.of());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            System.out.println("ERROR in getComments: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "댓글을 불러오는데 실패했습니다.");
            response.put("data", List.of());
            return ResponseEntity.status(500).body(response);
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

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "댓글이 작성되었습니다.");
            response.put("data", createdComment);
            return ResponseEntity.ok(response);
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
            System.err.println("Error in createComment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "댓글 작성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 댓글 수정 API
     * PUT /api/comments/{commentId}
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        System.out.println("=== COMMENT UPDATE METHOD ENTERED ===");
        System.out.println("CommentId: " + commentId);
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
            
            boolean updated = commentService.updateComment(commentId, user.getId(), commentDto.getContent().trim());

            if (updated) {
                // 수정된 댓글 정보를 반환
                Optional<Comment> updatedComment = commentRepository.findById(commentId);
                if (updatedComment.isPresent()) {
                    // CommentService의 convertToDto 메서드를 사용하기 위해 새로운 CommentDto 생성
                    Comment comment = updatedComment.get();
                    CommentDto updatedCommentDto = new CommentDto();
                    updatedCommentDto.setId(comment.getId());
                    updatedCommentDto.setContent(comment.getContent());
                    updatedCommentDto.setNickname(comment.getNickname());
                    updatedCommentDto.setUserId(comment.getUser().getId());
                    updatedCommentDto.setCreatedAt(comment.getCreatedAt());
                    updatedCommentDto.setUpdatedAt(comment.getUpdatedAt());
                    updatedCommentDto.setEdited(comment.getUpdatedAt() != null && !comment.getUpdatedAt().equals(comment.getCreatedAt()));
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "댓글이 수정되었습니다.");
                    response.put("data", updatedCommentDto);
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "댓글이 수정되었습니다.");
                    return ResponseEntity.ok(response);
                }
            } else {
                return ResponseEntity.status(403).body(Map.of("error", "댓글을 수정할 권한이 없습니다."));
            }
        } catch (Exception e) {
            System.err.println("Error in updateComment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "댓글 수정 중 오류가 발생했습니다: " + e.getMessage()));
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
        
        System.out.println("=== COMMENT DELETE METHOD ENTERED ===");
        System.out.println("CommentId: " + commentId);
        System.out.println("UserDetails: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        if (userDetails == null) {
            System.out.println("ERROR: UserDetails is null - returning 401");
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            // userDetails에서 username을 가져와 User 엔티티를 조회합니다.
            User user = userService.findUserEntityByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            System.out.println("Found user: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            boolean deleted = commentService.deleteComment(commentId, user.getId());
            System.out.println("Delete result: " + deleted);

            if (deleted) {
                System.out.println("Comment deleted successfully");
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "댓글이 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("ERROR: No permission to delete comment");
                return ResponseEntity.status(403).body(Map.of("error", "댓글을 삭제할 권한이 없습니다."));
            }
        } catch (Exception e) {
            System.err.println("Error in deleteComment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "댓글 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
