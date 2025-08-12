package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.PostDto;
import com.example.hyuk_blog.entity.Category;
import com.example.hyuk_blog.entity.PostKr;
import com.example.hyuk_blog.entity.PostJp;
import com.example.hyuk_blog.repository.PostKrRepository;
import com.example.hyuk_blog.repository.PostJpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostKrRepository postKrRepository;
    @Autowired
    private PostJpRepository postJpRepository;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CommentService commentService;

    // 언어 감지 (ko/ja)
    private String detectLang() {
        String lang = null;
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            HttpSession session = attr.getRequest().getSession(false);
            if (session != null) {
                Object langObj = session.getAttribute("lang");
                if (langObj != null) lang = langObj.toString();
            }
        }
        if (lang == null) {
            lang = LocaleContextHolder.getLocale().getLanguage();
        }
        return lang;
    }

    // 모든 공개 게시글 조회 (lang 파라미터 명시)
    public List<PostDto> getAllPublishedPosts(String lang) {
        if ("ja".equals(lang)) {
            return postJpRepository.findByPublishedOrderByCreatedAtDesc(true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromJpEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.JP)));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                    dto.setLang("ja"); // 언어 정보 추가
                    return dto;
                })
                .collect(Collectors.toList());
        } else {
            return postKrRepository.findByPublishedOrderByCreatedAtDesc(true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromKrEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.KR)));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                    dto.setLang("ko"); // 언어 정보 추가
                    return dto;
                })
                .collect(Collectors.toList());
        }
    }

    // lang 자동 감지
    public List<PostDto> getAllPublishedPosts() {
        return getAllPublishedPosts(detectLang());
    }

    // 모든 게시글 조회 (관리자용)
    public List<PostDto> getAllPosts(String lang) {
        System.out.println("=== POST SERVICE DEBUG ===");
        System.out.println("[PostService] getAllPosts called with lang: " + lang);
        
        if ("ja".equals(lang)) {
            System.out.println("[PostService] Fetching Japanese posts...");
            List<PostDto> posts = postJpRepository.findAllOrderByCreatedAtDesc()
                    .stream()
                    .map(post -> {
                        PostDto dto = PostDto.fromJpEntity(post);
                        dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.JP)));
                        dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                        dto.setLang("ja"); // 언어 정보 추가
                        return dto;
                    })
                    .collect(Collectors.toList());
            System.out.println("[PostService] Retrieved " + posts.size() + " Japanese posts");
            return posts;
        } else {
            System.out.println("[PostService] Fetching Korean posts...");
            List<PostDto> posts = postKrRepository.findAllOrderByCreatedAtDesc()
                    .stream()
                    .map(post -> {
                        PostDto dto = PostDto.fromKrEntity(post);
                        dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.KR)));
                        dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                        dto.setLang("ko"); // 언어 정보 추가
                        return dto;
                    })
                    .collect(Collectors.toList());
            System.out.println("[PostService] Retrieved " + posts.size() + " Korean posts");
            return posts;
        }
    }

    // ID로 게시글 조회
    public Optional<PostDto> getPostById(Long id, String lang) {
        System.out.println("=== POST SERVICE DEBUG ===");
        System.out.println("[PostService] getPostById called with id: " + id + ", lang: " + lang);
        
        if ("ja".equals(lang)) {
            System.out.println("[PostService] Fetching Japanese post...");
            return postJpRepository.findById(id).map(post -> {
                PostDto dto = PostDto.fromJpEntity(post);
                dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.JP)));
                dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                dto.setLang("ja"); // 언어 정보 추가
                System.out.println("[PostService] Japanese post found - ID: " + dto.getId() + ", titleJa: " + dto.getTitleJa());
                return dto;
            });
        } else {
            System.out.println("[PostService] Fetching Korean post...");
            return postKrRepository.findById(id).map(post -> {
                PostDto dto = PostDto.fromKrEntity(post);
                dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.KR)));
                dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                dto.setLang("ko"); // 언어 정보 추가
                System.out.println("[PostService] Korean post found - ID: " + dto.getId() + ", titleKo: " + dto.getTitleKo());
                return dto;
            });
        }
    }

    // 게시글 저장
    public PostDto savePost(PostDto postDto, String lang) {
        // published 상태는 PostDto에서 받은 값을 그대로 사용 (임시저장 지원)
        if ("ja".equals(lang)) {
            // 오직 posts_jp에만 저장
            PostJp saved = postJpRepository.save(postDto.toJpEntity());
            return PostDto.fromJpEntity(saved);
        } else if ("ko".equals(lang)) {
            // 오직 posts_kr에만 저장
            PostKr saved = postKrRepository.save(postDto.toKrEntity());
            return PostDto.fromKrEntity(saved);
        } else {
            // 예외 상황: 잘못된 lang 값이 들어온 경우
            System.err.println("[PostService] savePost: Unexpected lang value: " + lang);
            throw new IllegalArgumentException("Unsupported language: " + lang);
        }
    }

    // 게시글 수정
    public Optional<PostDto> updatePost(Long id, PostDto postDto, String lang) {
        if ("ja".equals(lang)) {
            return postJpRepository.findById(id).map(existing -> {
                existing.setTitle(postDto.getTitleJa());
                existing.setSummary(postDto.getSummaryJa());
                existing.setContent(postDto.getContentJa());
                existing.setImageUrl(postDto.getImageUrl());
                existing.setPublished(postDto.isPublished());
                existing.setCategory(postDto.getCategory());
                return PostDto.fromJpEntity(postJpRepository.save(existing));
            });
        } else {
            return postKrRepository.findById(id).map(existing -> {
                existing.setTitle(postDto.getTitleKo());
                existing.setSummary(postDto.getSummaryKo());
                existing.setContent(postDto.getContentKo());
                existing.setImageUrl(postDto.getImageUrl());
                existing.setPublished(postDto.isPublished());
                existing.setCategory(postDto.getCategory());
                return PostDto.fromKrEntity(postKrRepository.save(existing));
            });
        }
    }

    // 게시글 삭제
    public boolean deletePost(Long id, String lang) {
        System.out.println("=== DELETE POST SERVICE DEBUG ===");
        System.out.println("[PostService] deletePost called with id: " + id + ", lang: " + lang);
        
        try {
            if ("ja".equals(lang)) {
                System.out.println("[PostService] Attempting to delete Japanese post...");
                if (postJpRepository.existsById(id)) {
                    System.out.println("[PostService] Japanese post found, deleting...");
                    // 댓글과 좋아요 먼저 삭제
                    commentService.deleteCommentsByPostJpId(id);
                    likeService.deleteLikesByPostId(id, com.example.hyuk_blog.entity.PostType.JP);
                    postJpRepository.deleteById(id);
                    System.out.println("[PostService] Japanese post deleted successfully");
                    return true;
                } else {
                    System.out.println("[PostService] Japanese post not found with ID: " + id);
                }
            } else {
                System.out.println("[PostService] Attempting to delete Korean post...");
                if (postKrRepository.existsById(id)) {
                    System.out.println("[PostService] Korean post found, deleting...");
                    // 댓글과 좋아요 먼저 삭제
                    commentService.deleteCommentsByPostKrId(id);
                    likeService.deleteLikesByPostId(id, com.example.hyuk_blog.entity.PostType.KR);
                    postKrRepository.deleteById(id);
                    System.out.println("[PostService] Korean post deleted successfully");
                    return true;
                } else {
                    System.out.println("[PostService] Korean post not found with ID: " + id);
                }
            }
            System.out.println("[PostService] Post not found for deletion");
            return false;
        } catch (Exception e) {
            System.err.println("게시글 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 제목으로 검색 (공개된 게시글만)
    public List<PostDto> searchPublishedPosts(String query, String lang) {
        if ("ja".equals(lang)) {
            return postJpRepository.findByTitleContainingAndPublishedOrderByCreatedAtDesc(query, true)
                    .stream()
                    .map(post -> {
                        PostDto dto = PostDto.fromJpEntity(post);
                        dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.JP)));
                        dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            return postKrRepository.findByTitleContainingAndPublishedOrderByCreatedAtDesc(query, true)
                    .stream()
                    .map(post -> {
                        PostDto dto = PostDto.fromKrEntity(post);
                        dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.KR)));
                        dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    // 카테고리로 공개된 게시글 조회
    public List<PostDto> getPublishedPostsByCategory(Category category, String lang) {
        if ("ja".equals(lang)) {
            return postJpRepository.findByCategoryAndPublishedOrderByCreatedAtDesc(category, true)
                    .stream()
                    .map(post -> {
                        PostDto dto = PostDto.fromJpEntity(post);
                        dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.JP)));
                        dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            return postKrRepository.findByCategoryAndPublishedOrderByCreatedAtDesc(category, true)
                    .stream()
                    .map(post -> {
                        PostDto dto = PostDto.fromKrEntity(post);
                        dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(com.example.hyuk_blog.entity.PostType.KR)));
                        dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    // 관리자 대시보드 통계 조회
    public Map<String, Object> getAdminStats(String lang) {
        Map<String, Object> stats = new HashMap<>();
        
        if ("ja".equals(lang)) {
            long totalPosts = postJpRepository.count();
            long publishedPosts = postJpRepository.countByPublished(true);
            long draftPosts = postJpRepository.countByPublished(false);
            
            stats.put("totalPosts", totalPosts);
            stats.put("publishedPosts", publishedPosts);
            stats.put("draftPosts", draftPosts);
        } else {
            long totalPosts = postKrRepository.count();
            long publishedPosts = postKrRepository.countByPublished(true);
            long draftPosts = postKrRepository.countByPublished(false);
            
            stats.put("totalPosts", totalPosts);
            stats.put("publishedPosts", publishedPosts);
            stats.put("draftPosts", draftPosts);
        }
        
        return stats;
    }
}