package com.example.hyuk_blog.service;

import com.example.hyuk_blog.entity.Like;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LikeService {
    
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Transactional
    public boolean toggleLike(Long postId, String postTypeStr, Long userId) {
        PostType postType = PostType.valueOf(postTypeStr.toUpperCase()); // 서비스 내부에서 변환
        boolean exists = likeRepository.existsByPostIdAndPostTypeAndUserId(postId, postType, userId);
        
        if (exists) {
            // 좋아요 취소
            likeRepository.deleteByPostIdAndPostTypeAndUserId(postId, postType, userId);
            return false;
        } else {
            // 좋아요 추가
            Like like = new Like();
            like.setPostId(postId);
            like.setPostType(postType);
            like.setUserId(userId);
            safeSave(like);
            return true;
        }
    }
    
    public long getLikeCount(Long postId, String postTypeStr) {
        try {
            PostType postType = PostType.valueOf(postTypeStr.toUpperCase()); // 서비스 내부에서 변환
            logger.info("Getting like count for postId: {}, postType: {}", postId, postType);
            long count = likeRepository.countByPostIdAndPostType(postId, postType);
            logger.info("Like count: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error getting like count for postId: {}, postType: {}", postId, postTypeStr, e);
            throw e;
        }
    }
    
    public boolean isLikedByUser(Long postId, String postTypeStr, Long userId) {
        try {
            // userId가 null이면 좋아요하지 않은 것으로 처리
            if (userId == null) {
                logger.info("User ID is null, returning false for isLikedByUser");
                return false;
            }
            PostType postType = PostType.valueOf(postTypeStr.toUpperCase()); // 서비스 내부에서 변환
            logger.info("Checking if user {} liked postId: {}, postType: {}", userId, postId, postType);
            boolean isLiked = likeRepository.existsByPostIdAndPostTypeAndUserId(postId, postType, userId);
            logger.info("Is liked by user: {}", isLiked);
            return isLiked;
        } catch (Exception e) {
            logger.error("Error checking if user {} liked postId: {}, postType: {}", userId, postId, postTypeStr, e);
            throw e;
        }
    }
    
    /**
     * Detached Entity 문제를 방지하기 위한 안전한 저장 메서드
     */
    private Like safeSave(Like like) {
        try {
            return likeRepository.save(like);
        } catch (Exception e) {
            logger.warn("Detached entity 오류 발생, 새로운 엔티티로 재생성: {}", e.getMessage());
            // detached entity 오류 발생 시 새로운 엔티티로 재생성
            Like newLike = new Like();
            newLike.setPostId(like.getPostId());
            newLike.setPostType(like.getPostType());
            newLike.setUserId(like.getUserId());
            return likeRepository.save(newLike);
        }
    }
    
    /**
     * 게시글의 모든 좋아요 삭제
     */
    @Transactional
    public void deleteLikesByPostId(Long postId, PostType postType) {
        likeRepository.deleteByPostIdAndPostType(postId, postType);
    }
} 