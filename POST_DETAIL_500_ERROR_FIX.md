# Post-Detail 페이지 500 에러 해결 가이드

## 문제 분석

Post-detail 페이지에서 500 에러가 발생하는 주요 원인들을 분석했습니다:

### 1. PostDto의 LocaleContextHolder 문제
- `getTitle()`, `getSummary()`, `getContent()` 메소드들이 `LocaleContextHolder`를 사용
- API 컨트롤러에서는 이 컨텍스트가 제대로 설정되지 않을 수 있음

### 2. 언어 정보 누락
- PostService에서 언어 정보를 PostDto에 설정하지 않음
- 프론트엔드에서 언어별 데이터에 접근할 때 문제 발생

### 3. 예외 처리 부족
- API 컨트롤러에서 예외 처리가 충분하지 않음
- 프론트엔드에서 에러 정보를 제대로 표시하지 못함

## 해결 방법

### 1. PostDto 수정

**파일**: `hyuk_blog/src/main/java/com/example/hyuk_blog/dto/PostDto.java`

```java
// 언어 파라미터를 받는 메소드들 추가
public String getTitle(String lang) {
    if ("ja".equals(lang)) {
        return titleJa;
    } else {
        return titleKo;
    }
}

public String getSummary(String lang) {
    if ("ja".equals(lang)) {
        return summaryJa;
    } else {
        return summaryKo;
    }
}

public String getContent(String lang) {
    if ("ja".equals(lang)) {
        return contentJa;
    } else {
        return contentKo;
    }
}
```

### 2. PostService 수정

**파일**: `hyuk_blog/src/main/java/com/example/hyuk_blog/service/PostService.java`

```java
public Optional<PostDto> getPostById(Long id, String lang) {
    System.out.println("=== POST SERVICE DEBUG ===");
    System.out.println("[PostService] getPostById called with id: " + id + ", lang: " + lang);
    
    if ("ja".equals(lang)) {
        return postJpRepository.findById(id).map(post -> {
            PostDto dto = PostDto.fromJpEntity(post);
            dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(PostType.JP)));
            dto.setCommentCount(commentService.getCommentCountByPostJpId(post.getId()));
            dto.setLang("ja"); // 언어 정보 추가
            return dto;
        });
    } else {
        return postKrRepository.findById(id).map(post -> {
            PostDto dto = PostDto.fromKrEntity(post);
            dto.setLikeCount(likeService.getLikeCount(post.getId(), String.valueOf(PostType.KR)));
            dto.setCommentCount(commentService.getCommentCountByPostKrId(post.getId()));
            dto.setLang("ko"); // 언어 정보 추가
            return dto;
        });
    }
}
```

### 3. PostApiController 수정

**파일**: `hyuk_blog/src/main/java/com/example/hyuk_blog/api/controller/PostApiController.java`

```java
@GetMapping("/posts/{id}")
public ResponseEntity<Map<String, Object>> getPostById(
        @PathVariable Long id,
        @RequestParam(defaultValue = "ko") String lang) {
    
    System.out.println("=== POST DETAIL API DEBUG ===");
    System.out.println("[PostApiController] getPostById called with id: " + id + ", lang: " + lang);
    
    Map<String, Object> response = new HashMap<>();
    
    try {
        Optional<PostDto> post = postService.getPostById(id, lang);
        
        if (post.isPresent()) {
            PostDto postData = post.get();
            // 언어별 제목, 요약, 내용 설정
            String title = postData.getTitle(lang);
            String summary = postData.getSummary(lang);
            String content = postData.getContent(lang);
            
            response.put("success", true);
            response.put("data", postData);
            response.put("message", "게시글을 성공적으로 조회했습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "게시글을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    } catch (Exception e) {
        System.err.println("[PostApiController] Exception occurred: " + e.getMessage());
        e.printStackTrace();
        response.put("success", false);
        response.put("message", "게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

### 4. PostDetailView 수정

**파일**: `frontend/src/views/PostDetailView.vue`

```javascript
// Computed properties for safe data access
const currentTitle = computed(() => {
  const lang = route.query.lang || locale.value;
  if (lang === 'ja' && post.value.titleJa) {
    return post.value.titleJa;
  }
  return post.value.titleKo || post.value.title || '제목 없음';
});

const currentSummary = computed(() => {
  const lang = route.query.lang || locale.value;
  if (lang === 'ja' && post.value.summaryJa) {
    return post.value.summaryJa;
  }
  return post.value.summaryKo || post.value.summary || '';
});

const currentContent = computed(() => {
  const lang = route.query.lang || locale.value;
  if (lang === 'ja' && post.value.contentJa) {
    return post.value.contentJa;
  }
  return post.value.contentKo || post.value.content || '<p>내용이 없습니다.</p>';
});
```

### 5. API 서비스 개선

**파일**: `frontend/src/services/api.js`

```javascript
async request(endpoint, options = {}) {
  const url = `${this.baseURL}${endpoint}`;
  console.log('API Request URL:', url);
  
  try {
    const response = await fetch(url, config);
    console.log('API Response status:', response.status);
    
    if (!response.ok) {
      // 응답 본문을 읽어서 더 자세한 에러 정보 제공
      let errorMessage = `HTTP error! status: ${response.status}`;
      try {
        const errorBody = await response.text();
        if (errorBody) {
          try {
            const errorJson = JSON.parse(errorBody);
            errorMessage = errorJson.message || errorJson.error || errorMessage;
          } catch (e) {
            errorMessage = errorBody || errorMessage;
          }
        }
      } catch (e) {
        console.error('Failed to read error response body:', e);
      }
      
      throw new Error(errorMessage);
    }
    
    const data = await response.json();
    console.log('API Response data:', data);
    return data;
  } catch (error) {
    console.error('API Request failed:', error);
    throw error;
  }
}
```

### 6. GlobalExceptionHandler 개선

**파일**: `hyuk_blog/src/main/java/com/example/hyuk_blog/controller/GlobalExceptionHandler.java`

```java
@ExceptionHandler(Exception.class)
@ResponseBody
public ResponseEntity<Map<String, Object>> handleApiException(Exception ex) {
    System.err.println("=== API EXCEPTION HANDLER ===");
    System.err.println("Exception type: " + ex.getClass().getName());
    System.err.println("Error message: " + ex.getMessage());
    ex.printStackTrace();
    
    Map<String, Object> response = new HashMap<>();
    response.put("success", false);
    response.put("message", "서버 내부 오류가 발생했습니다: " + ex.getMessage());
    response.put("error", ex.getClass().getSimpleName());
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
}
```

## 테스트 방법

### 1. 백엔드 서버 실행
```bash
cd hyuk_blog
./gradlew bootRun
```

### 2. 프론트엔드 서버 실행
```bash
cd frontend
npm run serve
```

### 3. 브라우저에서 테스트
- `http://localhost:8080/knowledge` 접속
- 게시글 클릭하여 상세 페이지 확인
- 브라우저 개발자 도구에서 콘솔 로그 확인

## 로그 확인

### 백엔드 로그
```
=== POST DETAIL API DEBUG ===
[PostApiController] getPostById called with id: 1, lang: ko
=== POST SERVICE DEBUG ===
[PostService] getPostById called with id: 1, lang: ko
[PostService] Fetching Korean post...
[PostService] Korean post found - ID: 1, titleKo: 테스트 게시글
[PostApiController] Post found - ID: 1, titleKo: 있음, titleJa: 없음, lang: ko
[PostApiController] Processed data - title: 테스트 게시글, summary: 있음, content: 있음
```

### 프론트엔드 로그
```
=== POST DETAIL VIEW DEBUG ===
Loading post with ID: 1
Using language: ko
API Request URL: /api/posts/1?lang=ko
API Response status: 200
API Response data: {success: true, data: {...}}
Post data loaded: {id: 1, titleKo: "테스트 게시글", ...}
```

## 예상 결과

1. **500 에러 해결**: PostDto의 LocaleContextHolder 문제 해결
2. **언어별 데이터 정상 표시**: 한국어/일본어 게시글 정상 로드
3. **에러 메시지 개선**: 더 자세한 에러 정보 제공
4. **로깅 강화**: 문제 발생 시 디버깅 정보 확보

## 추가 개선 사항

1. **캐싱**: 자주 조회되는 게시글에 대한 캐싱 적용
2. **로딩 상태**: 더 나은 사용자 경험을 위한 로딩 인디케이터
3. **에러 페이지**: 사용자 친화적인 에러 페이지 구현
4. **성능 최적화**: 불필요한 API 호출 최소화
