package com.example.hyuk_blog.config;

import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.service.CustomUserDetailsService;
import com.example.hyuk_blog.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 정적 리소스는 JWT 검증 건너뛰기
        String requestURI = request.getRequestURI();
        if (isStaticResource(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                try {
                    if (jwtUtil.validateToken(jwt, jwtUtil.extractUsername(jwt))) {
                        String username = jwtUtil.extractUsername(jwt);
                        
                        // UserDetails를 통해 User 객체 가져오기
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("JWT 인증 성공: {}", username);
                    }
                } catch (Exception e) {
                    log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
                    // 토큰이 유효하지 않아도 요청은 계속 진행
                }
            }
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 예상치 못한 오류 발생: {}", e.getMessage());
            // 예상치 못한 오류가 발생해도 요청은 계속 진행
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isStaticResource(String requestURI) {
        return requestURI.startsWith("/css/") || 
               requestURI.startsWith("/js/") || 
               requestURI.startsWith("/img/") || 
               requestURI.startsWith("/svg/") || 
               requestURI.startsWith("/cursor/") || 
               requestURI.endsWith(".html") || 
               requestURI.equals("/favicon.ico") ||
               requestURI.startsWith("/api/auth/");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 