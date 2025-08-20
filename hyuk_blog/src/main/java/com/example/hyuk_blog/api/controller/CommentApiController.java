package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.CommentDto;
import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.entity.Comment;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.entity.Admin;
import com.example.hyuk_blog.repository.CommentRepository;
import com.example.hyuk_blog.service.CommentService;
import com.example.hyuk_blog.service.UserService;
import com.example.hyuk_blog.service.AdminService;
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
    private AdminService adminService;



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


        
        // 파라미터 검증
        if (postId == null) {
            return ResponseEntity.badRequest().body(List.of());
        }
        if (postTypeStr == null || postTypeStr.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());
        }

        try {
            List<CommentDto> comments = commentService.getCommentsByPost(postId, postTypeStr);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", comments);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "잘못된 파라미터입니다.");
            response.put("data", List.of());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
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
        

        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        if (commentDto.getContent() == null || commentDto.getContent().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "댓글 내용을 입력해주세요."));
        }

        try {
            String username = userDetails.getUsername();
            
            // 1. User 테이블에서 사용자 찾기
            Optional<User> userOpt = userService.findUserEntityByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
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
            }
            
            // 2. User 테이블에 없으면 Admin 테이블에서 확인
            Optional<Admin> adminOpt = adminService.findAdminEntityByUsername(username);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                // Admin 사용자의 경우 User 테이블에 없는 ID를 사용하므로 
                // CommentService에서 null로 처리되도록 함
                CommentDto createdComment = commentService.createComment(
                    postId, 
                    postTypeStr, 
                    commentDto.getContent().trim(), 
                    admin.getId(), 
                    admin.getUsername() // admin username을 nickname으로 사용
                );

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "댓글이 작성되었습니다.");
                response.put("data", createdComment);
                return ResponseEntity.ok(response);
            }
            
            // 3. 둘 다 없으면 사용자를 찾을 수 없음
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
            
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
        

        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        if (commentDto.getContent() == null || commentDto.getContent().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "댓글 내용을 입력해주세요."));
        }

        try {
            String username = userDetails.getUsername();
            
            // 1. User 테이블에서 사용자 찾기
            Optional<User> userOpt = userService.findUserEntityByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean updated = commentService.updateComment(commentId, user.getId(), commentDto.getContent().trim());

                if (updated) {
                    return ResponseEntity.ok(Map.of("success", true, "message", "댓글이 수정되었습니다."));
                } else {
                    return ResponseEntity.status(403).body(Map.of("error", "댓글을 수정할 권한이 없습니다."));
                }
            }
            
            // 2. User 테이블에 없으면 Admin 테이블에서 확인
            Optional<Admin> adminOpt = adminService.findAdminEntityByUsername(username);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                boolean updated = commentService.updateComment(commentId, admin.getId(), commentDto.getContent().trim());

                if (updated) {
                    return ResponseEntity.ok(Map.of("success", true, "message", "댓글이 수정되었습니다."));
                } else {
                    return ResponseEntity.status(403).body(Map.of("error", "댓글을 수정할 권한이 없습니다."));
                }
            }
            
            // 3. 둘 다 없으면 사용자를 찾을 수 없음
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
            
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
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            String username = userDetails.getUsername();
            
            // 1. User 테이블에서 사용자 찾기
            Optional<User> userOpt = userService.findUserEntityByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean deleted = commentService.deleteComment(commentId, user.getId());
                
                if (deleted) {
                    return ResponseEntity.ok(Map.of("success", true, "message", "댓글이 삭제되었습니다."));
                } else {
                    return ResponseEntity.status(403).body(Map.of("error", "댓글을 삭제할 권한이 없습니다."));
                }
            }
            
            // 2. Admin 테이블에서 관리자 찾기
            Optional<Admin> adminOpt = adminService.findAdminEntityByUsername(username);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                boolean deleted = commentService.deleteComment(commentId, admin.getId());
                
                if (deleted) {
                    return ResponseEntity.ok(Map.of("success", true, "message", "댓글이 삭제되었습니다."));
                } else {
                    return ResponseEntity.status(403).body(Map.of("error", "댓글을 삭제할 권한이 없습니다."));
                }
            }
            
            // 3. 둘 다 없으면 사용자를 찾을 수 없음
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
            
        } catch (Exception e) {
            System.err.println("Error in deleteComment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "댓글 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자별 댓글 조회 API
     * GET /api/user/comments
     */
    @GetMapping("/user/comments")
    public ResponseEntity<?> getUserComments(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            String username = userDetails.getUsername();
            
            // 1. User 테이블에서 사용자 찾기
            Optional<User> userOpt = userService.findUserEntityByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                List<CommentDto> comments = commentService.getCommentsByUserId(user.getId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", comments);
                response.put("count", comments.size());
                return ResponseEntity.ok(response);
            }
            
            // 2. Admin 테이블에서 관리자 찾기
            Optional<Admin> adminOpt = adminService.findAdminEntityByUsername(username);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                List<CommentDto> comments = commentService.getCommentsByUserId(admin.getId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", comments);
                response.put("count", comments.size());
                return ResponseEntity.ok(response);
            }
            
            // 3. 둘 다 없으면 사용자를 찾을 수 없음
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
            
        } catch (Exception e) {
            System.err.println("Error in getUserComments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "댓글 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
