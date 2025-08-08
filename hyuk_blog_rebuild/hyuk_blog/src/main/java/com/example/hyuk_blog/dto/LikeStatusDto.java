package com.example.hyuk_blog.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeStatusDto {
    private Long postId;
    private Long likeCount;
    private boolean isLiked;
}
