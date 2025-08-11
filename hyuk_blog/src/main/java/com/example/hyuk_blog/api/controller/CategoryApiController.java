package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {

    /**
     * 사용 가능한 카테고리 목록 조회
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        try {
            List<Map<String, String>> categories = Arrays.stream(Category.values())
                    .map(category -> {
                        Map<String, String> categoryInfo = new HashMap<>();
                        categoryInfo.put("value", category.name());
                        categoryInfo.put("displayName", category.getDisplayName());
                        categoryInfo.put("displayNameJp", category.getDisplayNameJp());
                        return categoryInfo;
                    })
                    .collect(Collectors.toList());
            
            log.info("Available categories: {}", categories);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Failed to get categories: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
