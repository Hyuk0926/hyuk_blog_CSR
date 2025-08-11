package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.PostDto;
import com.example.hyuk_blog.entity.Category;
import com.example.hyuk_blog.entity.Resume;
import com.example.hyuk_blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.hyuk_blog.dto.ResumeDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.dto.AdminDto;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import com.example.hyuk_blog.dto.InquiryDto;
import com.example.hyuk_blog.service.InquiryService;
import com.example.hyuk_blog.service.LikeService;
import com.example.hyuk_blog.service.CommentService;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AdminController adminController; // this might need to be ResumeService directly if you want locale-aware loading for 'about' page.

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/about")
    public String about(@RequestParam(value = "lang", required = false, defaultValue = "ko") String lang, Model model) {
        Resume resume = adminController.getResume();
        model.addAttribute("resume", resume);
        model.addAttribute("lang", lang);
        return "about";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/index")
    public String index(@RequestParam(value = "lang", required = false, defaultValue = "ko") String lang, Model model) {
        List<PostDto> posts = postService.getAllPublishedPosts(lang);
        model.addAttribute("posts", posts);
        model.addAttribute("categories", Category.values());
        model.addAttribute("lang", lang);
        return "index";
    }

    @GetMapping("/jp")
    public String jpIndex(@RequestParam(value = "lang", required = false) String lang, Model model) {
        // lang 파라미터가 ko이면 index 페이지로 리다이렉트
        if ("ko".equals(lang)) {
            return "redirect:/index?lang=ko";
        }
        
        // 기본적으로 일본어 포스트 표시
        List<PostDto> posts = postService.getAllPublishedPosts("ja");
        model.addAttribute("posts", posts);
        model.addAttribute("categories", Category.values());
        model.addAttribute("lang", "ja");
        return "index";
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable Long id, @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang, Model model, HttpServletRequest request, HttpSession session) {
        Optional<PostDto> post = postService.getPostById(id, lang);
        if (post.isPresent()) {
            // user 또는 admin 세션 확인
            UserDto user = (UserDto) session.getAttribute("user");
            AdminDto admin = (AdminDto) session.getAttribute("admin");
            boolean isLoggedIn = (user != null || admin != null);
            
            // 관리자가 아니고 게시글이 공개되지 않은 경우 리다이렉트
            if (!post.get().isPublished() && admin == null) {
                return "redirect:" + (lang.equals("ja") ? "/jp" : "/index?lang=" + lang);
            }
            
            // 게시글 타입에 따라 좋아요/댓글 수 조회
            Long userId = user != null ? user.getId() : (admin != null ? admin.getId() : null);
            long likeCount = 0;
            boolean isLiked = false;
            
            if (lang.equals("ja")) {
                // 일본어 게시글
                likeCount = likeService.getLikeCount(id, String.valueOf(com.example.hyuk_blog.entity.PostType.JP));
                if (userId != null) {
                    isLiked = likeService.isLikedByUser(id, String.valueOf(com.example.hyuk_blog.entity.PostType.JP), userId);
                }
            } else {
                // 한국어 게시글
                likeCount = likeService.getLikeCount(id, String.valueOf(com.example.hyuk_blog.entity.PostType.KR));
                if (userId != null) {
                    isLiked = likeService.isLikedByUser(id, String.valueOf(com.example.hyuk_blog.entity.PostType.KR), userId);
                }
            }
            
            model.addAttribute("post", post.get());
            model.addAttribute("lang", lang);
            model.addAttribute("likeCount", likeCount);
            model.addAttribute("isLiked", isLiked);
            model.addAttribute("postId", id);
            model.addAttribute("user", user);
            model.addAttribute("admin", admin);
            model.addAttribute("isLoggedIn", isLoggedIn);
            return "post-detail";
        }
        return "redirect:" + (lang.equals("ja") ? "/jp" : "/index?lang=" + lang);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @GetMapping("/projects")
    public String projects(@RequestParam(value = "lang", required = false, defaultValue = "ko") String lang, Model model) {
        model.addAttribute("lang", lang);
        return "projects";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang, Model model) {
        List<PostDto> posts = postService.searchPublishedPosts(q, lang);
        model.addAttribute("posts", posts);
        model.addAttribute("searchQuery", q);
        model.addAttribute("lang", lang);
        return "search";
    }

    @GetMapping("/resume")
    public String resume(Model model) {
        ResumeDto resume = new ResumeDto();
        model.addAttribute("resume", resume);
        return "resume";
    }

    @GetMapping("/resume/ko")
    public String resumeKo(Model model) {
        model.addAttribute("resume", adminController.getResume());
        return "resume-ko";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @PostMapping("/contact")
    public String contactSubmit(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String subject,
                                @RequestParam String message,
                                Model model) {
        // 문의 저장
        InquiryDto inquiry = new InquiryDto();
        inquiry.setName(name);
        inquiry.setEmail(email);
        inquiry.setSubject(subject);
        inquiry.setMessage(message);
        inquiryService.addInquiry(inquiry);
        model.addAttribute("successMessage", "문의가 성공적으로 접수되었습니다. 빠른 시일 내에 답변드리겠습니다.");
        return "contact";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public Map<String, Object> searchApi(@RequestParam String q, @RequestParam(value = "lang", required = false, defaultValue = "ko") String lang) {
        List<PostDto> posts = postService.searchPublishedPosts(q, lang);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", posts);
        response.put("message", "검색 결과를 성공적으로 조회했습니다.");
        return response;
    }
}