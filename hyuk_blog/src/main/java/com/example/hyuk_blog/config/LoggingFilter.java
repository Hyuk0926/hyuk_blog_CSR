package com.example.hyuk_blog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class LoggingFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    
    // 민감한 데이터가 포함된 파라미터들
    private static final List<String> SENSITIVE_PARAMS = Arrays.asList(
        "password", "token", "secret", "key", "auth", "jwt", "content", "title", "summary"
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 요청 URL 로깅 (민감한 정보 제외)
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        // 민감한 파라미터가 있는지 확인
        boolean hasSensitiveData = hasSensitiveParameters(httpRequest);
        
        if (hasSensitiveData) {
            logger.info("{} {} - [민감한 데이터 포함]", method, requestURI);
        } else {
            logger.info("{} {}", method, requestURI);
        }
        
        chain.doFilter(request, response);
        
        // 응답 상태 로깅
        logger.debug("Response Status: {}", httpResponse.getStatus());
    }
    
    private boolean hasSensitiveParameters(HttpServletRequest request) {
        return request.getParameterMap().keySet().stream()
                .anyMatch(paramName -> SENSITIVE_PARAMS.stream()
                        .anyMatch(sensitive -> paramName.toLowerCase().contains(sensitive.toLowerCase())));
    }
}
