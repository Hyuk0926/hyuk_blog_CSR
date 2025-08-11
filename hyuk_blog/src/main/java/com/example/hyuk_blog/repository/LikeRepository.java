package com.example.hyuk_blog.repository;

import com.example.hyuk_blog.entity.Like;
import com.example.hyuk_blog.entity.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.postId = :postId AND l.postType = :postType")
    long countByPostIdAndPostType(@Param("postId") Long postId, @Param("postType") PostType postType);
    
    Optional<Like> findByPostIdAndPostTypeAndUserId(Long postId, PostType postType, Long userId);
    
    boolean existsByPostIdAndPostTypeAndUserId(Long postId, PostType postType, Long userId);
    
//    void deleteByPostIdAndPostTypeAndUserId(Long postId, PostType postType, Long userId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.postId = :postId AND l.postType = :postType AND l.userId = :userId")
    void deleteByPostIdAndPostTypeAndUserId(@Param("postId") Long postId, @Param("postType") PostType postType, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.postId = :postId AND l.postType = :postType")
    void deleteByPostIdAndPostType(@Param("postId") Long postId, @Param("postType") PostType postType);
} 