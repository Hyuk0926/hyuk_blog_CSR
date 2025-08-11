package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.CommentDto;
import com.example.hyuk_blog.entity.Comment;
import com.example.hyuk_blog.entity.PostKr;
import com.example.hyuk_blog.entity.PostJp;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.repository.CommentRepository;
import com.example.hyuk_blog.repository.PostKrRepository;
import com.example.hyuk_blog.repository.PostJpRepository;
import com.example.hyuk_blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostKrRepository postKrRepository;
    
    @Autowired
    private PostJpRepository postJpRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 게시글 댓글 조회 (통합 메서드)
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPost(Long postId, String postTypeStr) {
        try {
            PostType postType = PostType.valueOf(postTypeStr.toUpperCase()); // 서비스 내부에서 변환
            logger.info("Getting comments for postId: {}, postType: {}", postId, postType);
            List<Comment> comments;
            if (postType == PostType.KR) {
                comments = commentRepository.findByPostKrIdOrderByCreatedAtAsc(postId);
            } else {
                comments = commentRepository.findByPostJpIdOrderByCreatedAtAsc(postId);
            }
            logger.info("Found {} comments", comments.size());
            // 서비스 내에서 DTO 변환을 완료하여 LazyInitializationException 방지
            return comments.stream()
                .map(this::convertToDto)
                .filter(dto -> dto != null) // null DTO 필터링
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting comments for postId: {}, postType: {}", postId, postTypeStr, e);
            throw e;
        }
    }
    
    // 한국어 게시글 댓글 조회 (기존 메서드 유지)
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostKrId(Long postId) {
        List<Comment> comments = commentRepository.findByPostKrIdOrderByCreatedAtAsc(postId);
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // 일본어 게시글 댓글 조회 (기존 메서드 유지)
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostJpId(Long postId) {
        List<Comment> comments = commentRepository.findByPostJpIdOrderByCreatedAtAsc(postId);
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Transactional
    public CommentDto createComment(Long postId, String postTypeStr, String content, Long userId, String nickname) {
        PostType postType = PostType.valueOf(postTypeStr.toUpperCase()); // 서비스 내부에서 변환
        logger.info("Creating comment - postId: {}, postType: {}, content: {}, userId: {}, nickname: {}", 
                   postId, postType, content, userId, nickname);
        
        try {
            Comment comment = new Comment();
            comment.setContent(content.trim());
            comment.setNickname(nickname);
            comment.setPostType(postType);
            
            // User 엔티티를 찾아서 설정
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            comment.setUser(user);
            
            // 게시글 타입에 따라 연결
            if (postType == PostType.KR) {
                PostKr postKr = postKrRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("PostKr not found with id: " + postId));
                comment.setPostKr(postKr);
                logger.info("Found PostKr with id: {}", postId);
            } else if (postType == PostType.JP) {
                PostJp postJp = postJpRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("PostJp not found with id: " + postId));
                comment.setPostJp(postJp);
                logger.info("Found PostJp with id: {}", postId);
            } else {
                throw new IllegalArgumentException("Invalid post type: " + postType);
            }
            
            Comment savedComment = commentRepository.save(comment);
            logger.info("Comment saved successfully: {}", savedComment.getId());
            
            return convertToDto(savedComment);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found while creating comment - postId: {}, postType: {}, error: {}", 
                        postId, postType, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating comment - postId: {}, postType: {}, error: {}", 
                        postId, postType, e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional
    public boolean updateComment(Long commentId, Long userId, String newContent) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            
            // 댓글 작성자이거나 admin 계정인 경우 수정 가능
            if (userId.equals(comment.getUser() != null ? comment.getUser().getId() : null) || isAdminUser(userId)) {
                comment.setContent(newContent.trim());
                commentRepository.save(comment);
                return true;
            }
        }
        return false;
    }
    
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            
            // 댓글 작성자이거나 admin 계정인 경우 삭제 가능
            if (userId.equals(comment.getUser() != null ? comment.getUser().getId() : null) || isAdminUser(userId)) {
                commentRepository.delete(comment);
                return true;
            }
        }
        return false;
    }
    
    // 한국어 게시글 댓글 수 조회
    public Long getCommentCountByPostKrId(Long postId) {
        return commentRepository.countByPostKrId(postId);
    }
    
    // 일본어 게시글 댓글 수 조회
    public Long getCommentCountByPostJpId(Long postId) {
        return commentRepository.countByPostJpId(postId);
    }
    
    // 게시글의 모든 댓글 삭제 (PostKr)
    @Transactional
    public void deleteCommentsByPostKrId(Long postId) {
        commentRepository.deleteByPostKrId(postId);
    }
    
    // 게시글의 모든 댓글 삭제 (PostJp)
    @Transactional
    public void deleteCommentsByPostJpId(Long postId) {
        commentRepository.deleteByPostJpId(postId);
    }
    
    // admin 계정인지 확인하는 메서드
    private boolean isAdminUser(Long userId) {
        return userId != null && (userId == 1 || userId == 2); // admin, admin_jp 계정 ID
    }
    
    private CommentDto convertToDto(Comment comment) {
        try {
            if (comment == null) {
                logger.warn("Comment is null, skipping conversion");
                return null;
            }
            return CommentDto.fromEntity(comment);
        } catch (Exception e) {
            logger.error("Error converting comment to DTO: {}", comment != null ? comment.getId() : "null", e);
            return null; // null을 반환하여 스트림에서 필터링되도록 함
        }
    }
} 