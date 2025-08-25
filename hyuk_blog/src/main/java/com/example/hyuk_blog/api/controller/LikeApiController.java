package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.LikeStatusDto;
import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.service.LikeService;
import com.example.hyuk_blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LikeApiController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    /**
     * 테스트용 간단한 메서드
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("=== TEST METHOD ENTERED ===");
        return ResponseEntity.ok("Test successful!");
    }

    /**
     * 좋아요 토글 API
     * POST /api/posts/{postId}/like
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @RequestParam(value = "postType", defaultValue = "KR") String postTypeStr,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        System.out.println("=== LIKE TOGGLE METHOD ENTERED ===");
        System.out.println("PostId: " + postId);
        System.out.println("PostType: " + postTypeStr);
        System.out.println("UserDetails: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }

        try {
            // userDetails에서 username을 가져와 User 엔티티를 조회합니다.
            User user = userService.findUserEntityByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            boolean isLiked = likeService.toggleLike(postId, postTypeStr, user.getId());
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "isLiked", isLiked,
                "message", isLiked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다."
            ));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("PostKr not found") || errorMessage.contains("PostJp not found")) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "게시글을 찾을 수 없습니다. (ID: " + postId + ", Type: " + postTypeStr + ")"
                ));
            } else if (errorMessage.contains("User not found")) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "사용자 정보를 찾을 수 없습니다."
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", errorMessage
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Error in toggleLike: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "좋아요 처리 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    /**
     * 좋아요 상태 및 개수 조회 API
     * GET /api/posts/{postId}/like
     */
    @GetMapping("/posts/{postId}/like")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long postId,
            @RequestParam(value = "postType", required = true) String postTypeStr) {
        
        System.out.println("=== GET LIKE STATUS METHOD ENTERED ===");
        System.out.println("PostId: " + postId);
        System.out.println("PostType: " + postTypeStr);
        
        // 파라미터 검증
        if (postId == null) {
            System.out.println("ERROR: postId is null");
            return ResponseEntity.badRequest().body(Map.of("error", "postId is required"));
        }
        if (postTypeStr == null || postTypeStr.trim().isEmpty()) {
            System.out.println("ERROR: postType is null or empty");
            return ResponseEntity.badRequest().body(Map.of("error", "postType is required"));
        }
        
        // SecurityContextHolder에서 UserDetails 가져오기
        UserDetails userDetails = null;
        try {
            userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println("UserDetails from SecurityContext: " + (userDetails != null ? userDetails.getUsername() : "null"));
        } catch (Exception e) {
            System.out.println("No authentication found in SecurityContext");
        }
        
        try {
            System.out.println("Calling likeService.getLikeCount...");
            Long likeCount = likeService.getLikeCount(postId, postTypeStr);
            System.out.println("Like count: " + likeCount);
            
            boolean isLiked = false;

            // 로그인한 사용자인 경우 좋아요 여부 확인
            if (userDetails != null && userDetails.getUsername() != null) {
                try {
                    System.out.println("Looking up user: " + userDetails.getUsername());
                    // userDetails에서 username을 가져와 User 엔티티를 조회합니다.
                    User user = userService.findUserEntityByUsername(userDetails.getUsername())
                            .orElse(null);
                    if (user != null) {
                        System.out.println("User found, checking if liked...");
                        isLiked = likeService.isLikedByUser(postId, postTypeStr, user.getId());
                        System.out.println("Is liked: " + isLiked);
                    } else {
                        System.out.println("User not found in database");
                    }
                } catch (Exception e) {
                    System.err.println("Error checking if user liked: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            LikeStatusDto response = new LikeStatusDto();
            response.setLiked(isLiked);
            response.setLikeCount(likeCount);
            
            System.out.println("Returning response: " + response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR in getLikeStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "좋아요 상태 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}
