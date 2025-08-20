package com.example.hyuk_blog.repository;

import com.example.hyuk_blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT c FROM Comment c WHERE c.postKr.id = :postId AND c.postType = 'KR' ORDER BY c.createdAt ASC")
    List<Comment> findByPostKrIdOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    @Query("SELECT c FROM Comment c WHERE c.postJp.id = :postId AND c.postType = 'JP' ORDER BY c.createdAt ASC")
    List<Comment> findByPostJpIdOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.postKr.id = :postId AND c.postType = 'KR'")
    Long countByPostKrId(@Param("postId") Long postId);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.postJp.id = :postId AND c.postType = 'JP'")
    Long countByPostJpId(@Param("postId") Long postId);
    
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.postKr.id = :postId AND c.postType = 'KR'")
    void deleteByPostKrId(@Param("postId") Long postId);
    
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.postJp.id = :postId AND c.postType = 'JP'")
    void deleteByPostJpId(@Param("postId") Long postId);
    
    /**
     * 사용자별 댓글 조회 (최신순)
     */
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * 사용자별 댓글 수 조회
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
} 