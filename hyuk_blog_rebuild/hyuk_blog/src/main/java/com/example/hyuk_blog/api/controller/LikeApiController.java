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
            @RequestParam(defaultValue = "KR") PostType postType,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Optional<UserDto> userOpt = userService.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("사용자 정보를 찾을 수 없습니다.");
        }
        UserDto userDto = userOpt.get();

        try {
            boolean isLiked = likeService.toggleLike(postId, postType, userDto.getId());
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
            @RequestParam(defaultValue = "KR") PostType postType,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long likeCount = likeService.getLikeCount(postId, postType);
        boolean isLiked = false;

        // 로그인한 사용자인 경우 좋아요 여부 확인
        if (userDetails != null) {
            Optional<UserDto> userOpt = userService.findByUsername(userDetails.getUsername());
            if (userOpt.isPresent()) {
                UserDto userDto = userOpt.get();
                isLiked = likeService.isLikedByUser(postId, postType, userDto.getId());
            }
        }

        LikeStatusDto likeStatus = new LikeStatusDto(postId, likeCount, isLiked);
        return ResponseEntity.ok(likeStatus);
    }
}
