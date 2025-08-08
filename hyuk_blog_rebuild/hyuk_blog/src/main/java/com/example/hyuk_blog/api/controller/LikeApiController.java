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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LikeApiController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    /**
     * 좋아요 토글 API
     * POST /api/posts/{postId}/like
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "KR") String postTypeStr,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
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
        } catch (Exception e) {
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
    public ResponseEntity<LikeStatusDto> getLikeStatus(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "KR") String postTypeStr,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long likeCount = likeService.getLikeCount(postId, postTypeStr);
            boolean isLiked = false;

            // 로그인한 사용자인 경우 좋아요 여부 확인
            if (userDetails != null) {
                try {
                    // userDetails에서 username을 가져와 User 엔티티를 조회합니다.
                    User user = userService.findUserEntityByUsername(userDetails.getUsername())
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
                    isLiked = likeService.isLikedByUser(postId, postTypeStr, user.getId());
                } catch (Exception e) {
                    // 사용자 정보를 찾을 수 없는 경우 isLiked는 false로 유지
                    isLiked = false;
                }
            }

            LikeStatusDto likeStatus = new LikeStatusDto(postId, likeCount, isLiked);
            return ResponseEntity.ok(likeStatus);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new LikeStatusDto(postId, 0L, false));
        }
    }
}
