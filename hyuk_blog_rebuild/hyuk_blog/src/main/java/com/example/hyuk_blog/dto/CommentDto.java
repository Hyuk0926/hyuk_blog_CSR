package com.example.hyuk_blog.dto;

import com.example.hyuk_blog.entity.Comment;
import com.example.hyuk_blog.entity.PostType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long postjpId;
    private Long postkrId;
//    private String postEncryptedId;
    private String nickname;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isEdited;

    /**
     * Comment 엔티티를 CommentDto로 변환하는 정적 메서드
     */
    public static CommentDto fromEntity(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setNickname(comment.getNickname());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUser() != null ? comment.getUser().getId() : null);
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setEdited(!comment.getCreatedAt().equals(comment.getUpdatedAt()));
        
        // 게시글 타입에 따라 ID 설정
        if (comment.getPostType() == PostType.KR && comment.getPostKr() != null) {
            dto.setPostkrId(comment.getPostKr().getId());
        } else if (comment.getPostType() == PostType.JP && comment.getPostJp() != null) {
            dto.setPostjpId(comment.getPostJp().getId());
        }
        
        return dto;
    }
} 