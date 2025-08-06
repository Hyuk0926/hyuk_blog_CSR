package com.example.hyuk_blog.dto.post;

import com.example.hyuk_blog.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCategoryResponseDto {
    private Category category;
    private String categoryDisplayName;
    private String categoryDisplayNameJp;
    private List<PostResponseDto> posts;
    private int totalCount;
    private String language;
} 