package com.example.hyuk_blog.config;

import com.example.hyuk_blog.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)  // CORS 비활성화 (WebMvcConfig에서 처리)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                // --- 정적 리소스 허용 ---
                .requestMatchers("/css/**", "/js/**", "/img/**", "/svg/**", "/favicon.ico").permitAll()
                
                // --- 업로드된 이미지 허용 ---
                .requestMatchers("/uploads/**").permitAll()
                
                // --- 프론트엔드 페이지 허용 ---
                .requestMatchers("/", "/home", "/user/**", "/password-reset", "/reset-password").permitAll()
                
                // --- GET 요청은 대부분 허용 ---
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/resume").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/test").permitAll() // 테스트용
                
                // --- 인증(로그인/회원가입) API 허용 ---
                .requestMatchers("/api/auth/**").permitAll()
                
                // --- 문의하기 API 허용 ---
                .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()

                // --- 비밀번호 재설정 API 허용 ---
                .requestMatchers("/api/password-reset/**").permitAll()

                // --- 관리자 페이지는 인증 필요 ---
                .requestMatchers("/admin/**").authenticated()
                
                // --- 그 외 모든 요청은 허용 (SPA이므로) ---
                .anyRequest().permitAll()
            )
            .userDetailsService(customUserDetailsService)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 사용할 암호화 알고리즘 ID 정의
        String idForEncode = "bcrypt";
        
        // 여러 암호화 방식을 담을 맵 생성
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("sha256", new Sha256PasswordEncoder());
        
        // DelegatingPasswordEncoder 생성. 기본 인코더는 BCrypt로 설정.
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
        
        // 기존 비밀번호(접두사 없는)는 BCrypt로 처리하도록 설정
        passwordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

        return passwordEncoder;
    }
    
    /**
     * SHA-256 비밀번호 인코더
     * 기존 SHA-256 해시를 지원하기 위한 커스텀 인코더
     */
    public static class Sha256PasswordEncoder implements PasswordEncoder {
        
        @Override
        public String encode(CharSequence rawPassword) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(rawPassword.toString().getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (Exception e) {
                throw new RuntimeException("SHA-256 인코딩 실패", e);
            }
        }
        
        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            String encodedRawPassword = encode(rawPassword);
            return encodedRawPassword.equals(encodedPassword);
        }
    }
} 