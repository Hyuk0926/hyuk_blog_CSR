package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.PostDto;
import com.example.hyuk_blog.dto.post.*;
import com.example.hyuk_blog.entity.Category;
import com.example.hyuk_blog.entity.PostCommon;
import com.example.hyuk_blog.entity.PostKr;
import com.example.hyuk_blog.entity.PostJp;
import com.example.hyuk_blog.entity.PostType;
import com.example.hyuk_blog.repository.PostKrRepository;
import com.example.hyuk_blog.repository.PostJpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    public List<PostDto> getAllPublishedPosts;
    @Autowired
    private PostKrRepository postKrRepository;
    
    @Autowired
    private PostJpRepository postJpRepository;
    
    @Autowired
    private LikeService likeService;
    
    @Autowired
    private CommentService commentService;

    // ==================== 언어 감지 및 유틸리티 메서드 ====================
    
    /**
     * 언어 감지 (ko/ja)
     */
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

    /**
     * 언어 파라미터를 PostType으로 변환
     */
    private PostType getPostTypeFromLang(String lang) {
        if ("ja".equalsIgnoreCase(lang) || "jp".equalsIgnoreCase(lang)) {
            return PostType.JP;
        } else {
            return PostType.KR;
        }
    }

    /**
     * PostType에 따른 적절한 Repository 선택
     */
    private PostKrRepository getKrRepository() {
        return postKrRepository;
    }

    private PostJpRepository getJpRepository() {
        return postJpRepository;
    }

    // ==================== 조회 메서드들 ====================
    
    /**
     * 모든 공개 게시글 조회 (언어 파라미터 기반)
     */
    public List<PostResponseDto> findAll(String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findByPublishedOrderByCreatedAtDesc(true)
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(dto.getId()));
                })
                .collect(Collectors.toList());
        } else {
            return getKrRepository().findByPublishedOrderByCreatedAtDesc(true)
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(dto.getId()));
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * 모든 공개 게시글 조회 (언어 자동 감지)
     */
    public List<PostResponseDto> findAll() {
        return findAll(detectLang());
    }

    /**
     * 모든 게시글 조회 (관리자용, 언어 파라미터 기반)
     */
    public List<PostResponseDto> findAllForAdmin(String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findAllOrderByCreatedAtDesc()
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(dto.getId()));
                })
                .collect(Collectors.toList());
        } else {
            return getKrRepository().findAllOrderByCreatedAtDesc()
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(dto.getId()));
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * ID로 게시글 조회 (언어 파라미터 기반)
     */
    public Optional<PostResponseDto> findById(Long id, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findById(id)
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .map(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(dto.getId()));
                    return dto;
                });
        } else {
            return getKrRepository().findById(id)
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .map(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(dto.getId()));
                    return dto;
                });
        }
    }

    /**
     * ID로 게시글 조회 (언어 자동 감지)
     */
    public Optional<PostResponseDto> findById(Long id) {
        return findById(id, detectLang());
    }

    /**
     * 제목으로 검색 (공개된 게시글만, 언어 파라미터 기반)
     */
    public List<PostResponseDto> searchByTitle(String query, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findByTitleContainingAndPublishedOrderByCreatedAtDesc(query, true)
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(dto.getId()));
                })
                .collect(Collectors.toList());
        } else {
            return getKrRepository().findByTitleContainingAndPublishedOrderByCreatedAtDesc(query, true)
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(dto.getId()));
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * 카테고리로 공개된 게시글 조회 (언어 파라미터 기반)
     */
    public List<PostResponseDto> findByCategory(Category category, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findByCategoryAndPublishedOrderByCreatedAtDesc(category, true)
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(dto.getId()));
                })
                .collect(Collectors.toList());
        } else {
            return getKrRepository().findByCategoryAndPublishedOrderByCreatedAtDesc(category, true)
                .stream()
                .map(post -> convertToPostResponseDto((PostCommon) post))
                .peek(dto -> {
                    dto.setLikeCount(likeService.getLikeCount(dto.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(dto.getId()));
                })
                .collect(Collectors.toList());
        }
    }

    // ==================== 저장/수정 메서드들 ====================
    
    /**
     * 게시글 저장 (언어 파라미터 기반)
     */
    public PostResponseDto save(PostCreateRequestDto requestDto, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            PostJp post = new PostJp();
            post.setTitle(requestDto.getTitle());
            post.setSummary(requestDto.getSummary());
            post.setContent(requestDto.getContent());
            post.setImageUrl(requestDto.getImageUrl());
            post.setPublished(requestDto.isPublished());
            post.setCategory(requestDto.getCategory());
            
            PostJp saved = getJpRepository().save(post);
            return convertToPostResponseDto(saved);
        } else {
            PostKr post = new PostKr();
            post.setTitle(requestDto.getTitle());
            post.setSummary(requestDto.getSummary());
            post.setContent(requestDto.getContent());
            post.setImageUrl(requestDto.getImageUrl());
            post.setPublished(requestDto.isPublished());
            post.setCategory(requestDto.getCategory());
            
            PostKr saved = getKrRepository().save(post);
            return convertToPostResponseDto(saved);
        }
    }

    /**
     * 게시글 저장 (언어 자동 감지)
     */
    public PostResponseDto save(PostCreateRequestDto requestDto) {
        return save(requestDto, detectLang());
    }

    /**
     * 게시글 수정 (언어 파라미터 기반)
     */
    public Optional<PostResponseDto> update(Long id, PostUpdateRequestDto requestDto, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findById(id)
                .map(existing -> {
                    existing.setTitle(requestDto.getTitle());
                    existing.setSummary(requestDto.getSummary());
                    existing.setContent(requestDto.getContent());
                    existing.setImageUrl(requestDto.getImageUrl());
                    existing.setPublished(requestDto.isPublished());
                    existing.setCategory(requestDto.getCategory());
                    
                    PostJp saved = getJpRepository().save(existing);
                    return convertToPostResponseDto(saved);
                });
        } else {
            return getKrRepository().findById(id)
                .map(existing -> {
                    existing.setTitle(requestDto.getTitle());
                    existing.setSummary(requestDto.getSummary());
                    existing.setContent(requestDto.getContent());
                    existing.setImageUrl(requestDto.getImageUrl());
                    existing.setPublished(requestDto.isPublished());
                    existing.setCategory(requestDto.getCategory());
                    
                    PostKr saved = getKrRepository().save(existing);
                    return convertToPostResponseDto(saved);
                });
        }
    }

    /**
     * 게시글 수정 (언어 자동 감지)
     */
    public Optional<PostResponseDto> update(Long id, PostUpdateRequestDto requestDto) {
        return update(id, requestDto, detectLang());
    }

    // ==================== 삭제 메서드들 ====================
    
    /**
     * 게시글 삭제 (언어 파라미터 기반)
     */
    public boolean deleteById(Long id, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            if (getJpRepository().existsById(id)) {
                getJpRepository().deleteById(id);
                return true;
            }
        } else {
            if (getKrRepository().existsById(id)) {
                getKrRepository().deleteById(id);
                return true;
            }
        }
        return false;
    }

    /**
     * 게시글 삭제 (언어 자동 감지)
     */
    public boolean deleteById(Long id) {
        return deleteById(id, detectLang());
    }

    // ==================== 변환 메서드들 ====================
    
    /**
     * PostCommon 인터페이스를 구현하는 엔티티를 PostResponseDto로 변환
     */
    private PostResponseDto convertToPostResponseDto(PostCommon post) {
        return PostResponseDto.fromPostCommon(post);
    }

    // ==================== 하위 호환성을 위한 기존 메서드들 ====================
    
    /**
     * 기존 메서드명과의 호환성을 위한 별칭 메서드들
     */
    public List<PostResponseDto> getAllPublishedPosts(String lang) {
        return findAll(lang);
    }

    public List<PostResponseDto> getAllPublishedPosts() {
        return findAll();
    }

    public List<PostResponseDto> getAllPosts(String lang) {
        return findAllForAdmin(lang);
    }

    public Optional<PostResponseDto> getPostById(Long id, String lang) {
        return findById(id, lang);
    }

    public PostResponseDto savePost(PostCreateRequestDto requestDto) {
        return save(requestDto);
    }

    public Optional<PostResponseDto> updatePost(Long id, PostUpdateRequestDto requestDto) {
        return update(id, requestDto);
    }

    public Optional<PostResponseDto> updatePost(Long id, PostDto postDto, String lang) {
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto();
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            requestDto.setTitle(postDto.getTitleJa());
            requestDto.setSummary(postDto.getSummaryJa());
            requestDto.setContent(postDto.getContentJa());
        } else {
            requestDto.setTitle(postDto.getTitleKo());
            requestDto.setSummary(postDto.getSummaryKo());
            requestDto.setContent(postDto.getContentKo());
        }
        
        requestDto.setImageUrl(postDto.getImageUrl());
        requestDto.setPublished(postDto.isPublished());
        requestDto.setCategory(postDto.getCategory());
        requestDto.setPostType(postType);
        
        return update(id, requestDto, lang);
    }

    public boolean deletePost(Long id, String lang) {
        return deleteById(id, lang);
    }

    public List<PostResponseDto> searchPublishedPosts(String query, String lang) {
        return searchByTitle(query, lang);
    }

    public List<PostResponseDto> getPublishedPostsByCategory(Category category, String lang) {
        return findByCategory(category, lang);
    }

    public List<PostDto> searchPublishedPostsAsPostDto(String query, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findByTitleContainingAndPublishedOrderByCreatedAtDesc(query, true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromJpEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        } else {
            return getKrRepository().findByTitleContainingAndPublishedOrderByCreatedAtDesc(query, true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromKrEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        }
    }

    public List<PostDto> getPublishedPostsByCategoryAsPostDto(Category category, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findByCategoryAndPublishedOrderByCreatedAtDesc(category, true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromJpEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        } else {
            return getKrRepository().findByCategoryAndPublishedOrderByCreatedAtDesc(category, true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromKrEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        }
    }

    // ==================== 기존 PostDto 호환성 메서드들 ====================
    
    /**
     * 기존 PostDto를 사용하는 메서드들 (하위 호환성)
     */
    public List<PostDto> getAllPublishedPostsAsPostDto(String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findByPublishedOrderByCreatedAtDesc(true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromJpEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        } else {
            return getKrRepository().findByPublishedOrderByCreatedAtDesc(true)
                .stream()
                .map(post -> {
                    PostDto dto = PostDto.fromKrEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        }
    }

    public Optional<PostDto> getPostByIdAsPostDto(Long id, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            return getJpRepository().findById(id)
                .map(post -> {
                    PostDto dto = PostDto.fromJpEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.JP));
                    dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
                    return dto;
                });
        } else {
            return getKrRepository().findById(id)
                .map(post -> {
                    PostDto dto = PostDto.fromKrEntity(post);
                    dto.setLikeCount(likeService.getLikeCount(post.getId(), PostType.KR));
                    dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
                    return dto;
                });
        }
    }

    public PostDto savePostAsPostDto(PostDto postDto, String lang) {
        PostType postType = getPostTypeFromLang(lang);
        
        if (PostType.JP.equals(postType)) {
            PostJp saved = getJpRepository().save(postDto.toJpEntity());
            return PostDto.fromJpEntity(saved);
        } else {
            PostKr saved = getKrRepository().save(postDto.toKrEntity());
            return PostDto.fromKrEntity(saved);
                 }
     }
 }