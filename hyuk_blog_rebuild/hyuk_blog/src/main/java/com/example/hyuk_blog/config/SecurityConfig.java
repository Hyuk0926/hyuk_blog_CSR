package com.example.hyuk_blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS 설정 추가
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. CSRF 보호 비활성화 (Stateless 서버에서는 불필요)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 3. 세션 관리 정책을 STATELESS로 설정 (가장 중요!)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4. HTTP 요청에 대한 인가 규칙 설정
            .authorizeHttpRequests(authorize -> authorize
                // ⭐ 핵심: 모든 OPTIONS 사전 요청은 인증 없이 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 정적 리소스 및 기본 페이지는 모두 허용
                .requestMatchers("/css/**", "/js/**", "/img/**", "/svg/**", "/cursor/**", "/favicon.ico", "/").permitAll()
                
                // 로그인, 회원가입 API는 모두 허용
                .requestMatchers("/api/auth/**").permitAll()
                
                // 게시글 조회 API는 모두 허용
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                
                // 사용자 정보 조회는 인증된 사용자만 접근 가능
                .requestMatchers("/api/user/**").authenticated()
                
                // 그 외 모든 API 요청은 인증된 사용자만 접근 가능
                .anyRequest().authenticated()
            )
            
            // 5. 기본 폼 로그인 및 로그아웃 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            
            // 6. 우리가 만든 JWT 필터를 Spring Security 필터 체인에 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 프론트엔드 포트들을 모두 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8080", "http://localhost:8082", "http://localhost:8083", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // PasswordEncoder를 Bean으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager를 Bean으로 등록하여 다른 곳에서 주입받을 수 있도록 함
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
} 