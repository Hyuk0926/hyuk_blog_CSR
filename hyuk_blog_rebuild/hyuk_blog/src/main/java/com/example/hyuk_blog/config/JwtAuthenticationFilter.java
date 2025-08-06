package com.example.hyuk_blog.config;

import com.example.hyuk_blog.service.CustomUserDetailsService;
import com.example.hyuk_blog.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        // 핵심: OPTIONS 요청은 JWT 검사를 하지 않고 바로 통과시킵니다.
        // CORS 사전 요청에는 Authorization 헤더가 없으므로 JWT 검사에서 제외해야 합니다.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("OPTIONS 요청 감지 - JWT 검사 건너뛰고 통과");
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 요청 헤더에서 JWT 토큰 추출
        String token = getJwtFromRequest(request);

        // 2. 토큰이 존재하고 유효한 경우
        if (StringUtils.hasText(token)) {
            try {
                // 3. 토큰에서 사용자 이름 추출
                String username = jwtUtil.extractUsername(token);
                
                if (jwtUtil.validateToken(token, username)) {

                    // 4. username으로 DB에서 UserDetails 객체 조회
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    // 5. UserDetails를 기반으로 Authentication 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 6. SecurityContextHolder에 Authentication 객체 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT 인증 성공: {}", username);
                }
            } catch (MalformedJwtException e) {
                log.error("유효하지 않은 JWT 서명입니다: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("JWT 클레임 문자열이 비어있습니다: {}", e.getMessage());
            } catch (Exception e) {
                log.error("JWT 인증 처리 중 알 수 없는 오류 발생: {}", e.getMessage());
            }
        }
        
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 