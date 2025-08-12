# 댓글과 좋아요 기능 500 에러 해결 가이드

## 1. 중복된 Exception Handler 문제 해결

### 문제 상황
```
Ambiguous @ExceptionHandler method mapped for [ExceptionHandler{exceptionType=java.lang.Exception, mediaType=*/*}]: 
{public org.springframework.http.ResponseEntity com.example.hyuk_blog.controller.GlobalExceptionHandler.handleApiException(java.lang.Exception), 
public java.lang.String com.example.hyuk_blog.controller.GlobalExceptionHandler.handleGenericException(java.lang.Exception,org.springframework.ui.Model)}
```

### 원인
`GlobalExceptionHandler` 클래스에서 두 개의 메서드가 모두 `@ExceptionHandler(Exception.class)`로 정의되어 있어서 Spring이 어떤 메서드를 사용할지 결정할 수 없었습니다:
- `handleGenericException` (String 반환)
- `handleApiException` (ResponseEntity 반환)

### 해결 방법
1. **중복된 예외 핸들러 제거**: `handleApiException` 메서드를 제거
2. **통합된 예외 처리**: `handleGenericException` 메서드를 수정하여 요청 타입에 따라 다른 응답을 반환하도록 수정

### 수정된 코드
```java
@ExceptionHandler(Exception.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public Object handleGenericException(Exception ex, Model model, WebRequest request) {
    // API 요청인지 확인 (Accept 헤더가 application/json이거나 /api로 시작하는 경로)
    String acceptHeader = request.getHeader("Accept");
    String requestUri = request.getDescription(false);
    
    if (acceptHeader != null && acceptHeader.contains("application/json") || 
        requestUri.contains("/api/")) {
        // API 요청에 대한 JSON 응답
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "서버 내부 오류가 발생했습니다: " + ex.getMessage());
        response.put("error", ex.getClass().getSimpleName());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } else {
        // 일반 웹 요청에 대한 HTML 응답
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "서버 오류가 발생했습니다");
        model.addAttribute("errorDescription", "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("exception", ex.getMessage());
        return "error";
    }
}
```

## 2. CORS 설정 충돌 문제 해결

### 문제 상황
```
When allowCredentials is true, allowedOrigins cannot contain the special value "*" 
since that cannot be set on the "Access-Control-Allow-Origin" response header. 
To allow credentials to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.
```

### 원인
- `WebMvcConfig`에서 `allowCredentials(true)`로 설정
- `LikeApiController`와 `CommentApiController`에서 `@CrossOrigin(origins = "*")` 사용
- Spring Security에서 credentials를 허용할 때는 와일드카드(`*`)를 사용할 수 없음

### 해결 방법
1. **중복 CORS 설정 제거**: 
   - `LikeApiController`에서 `@CrossOrigin(origins = "*")` 제거
   - `CommentApiController`에서 `@CrossOrigin(origins = "*")` 제거

2. **WebMvcConfig의 CORS 설정 유지**:
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://localhost:9090")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

### 수정된 파일
- `hyuk_blog/src/main/java/com/example/hyuk_blog/api/controller/LikeApiController.java`
- `hyuk_blog/src/main/java/com/example/hyuk_blog/api/controller/CommentApiController.java`

## 3. 해결 결과

이제 다음 기능들이 정상적으로 작동합니다:
- ✅ 좋아요 상태 조회 (`GET /api/posts/{postId}/like`)
- ✅ 좋아요 토글 (`POST /api/posts/{postId}/like`)
- ✅ 댓글 목록 조회 (`GET /api/posts/{postId}/comments`)
- ✅ 댓글 작성 (`POST /api/posts/{postId}/comments`)
- ✅ 댓글 삭제 (`DELETE /api/comments/{commentId}`)

## 4. 주의사항

1. **CORS 설정 중복 방지**: 컨트롤러 레벨에서 `@CrossOrigin`을 사용할 때는 전역 CORS 설정과 충돌하지 않도록 주의
2. **Exception Handler 중복 방지**: 같은 예외 타입에 대해 여러 핸들러를 정의하지 않도록 주의
3. **Credentials와 와일드카드**: `allowCredentials(true)`를 사용할 때는 `allowedOrigins("*")` 대신 구체적인 origin을 명시

## 5. 관련 파일

- `GlobalExceptionHandler.java` - 전역 예외 처리
- `WebMvcConfig.java` - CORS 설정
- `LikeApiController.java` - 좋아요 API
- `CommentApiController.java` - 댓글 API
