# Hyuk Blog DTO 구조

## 개요
REST API에 맞게 DTO 구조를 개선했습니다. 요청(Request)과 응답(Response) DTO를 분리하여 API의 명확성과 보안성을 향상시켰습니다.

## DTO 분류

### 1. 공통 DTO
- **ApiResponseDto<T>** - 모든 API 응답의 공통 래퍼 클래스

### 2. 게시글 관련 DTO
- **PostRequestDto** - 게시글 생성/수정 요청
- **PostResponseDto** - 게시글 응답
- **PostListResponseDto** - 게시글 목록 응답
- **PostDto** - 기존 게시글 DTO (내부 처리용)

### 3. 사용자 관련 DTO
- **UserRequestDto** - 사용자 생성/수정 요청
- **UserResponseDto** - 사용자 응답 (비밀번호 제외)
- **UserRegistrationDto** - 사용자 회원가입 요청
- **UserDto** - 기존 사용자 DTO (내부 처리용)

### 4. 관리자 관련 DTO
- **AdminRequestDto** - 관리자 생성/수정 요청
- **AdminResponseDto** - 관리자 응답 (비밀번호 제외)
- **AdminDto** - 기존 관리자 DTO (내부 처리용)

### 5. 댓글 관련 DTO
- **CommentRequestDto** - 댓글 생성/수정 요청
- **CommentResponseDto** - 댓글 응답
- **CommentDto** - 기존 댓글 DTO (내부 처리용)

### 6. 좋아요 관련 DTO
- **LikeResponseDto** - 좋아요 상태 응답

### 7. 문의 관련 DTO
- **InquiryRequestDto** - 문의 생성 요청
- **InquiryResponseDto** - 문의 응답
- **InquiryDto** - 기존 문의 DTO (내부 처리용)

### 8. 방문자 통계 관련 DTO
- **VisitorStatsResponseDto** - 방문자 통계 응답

### 9. 검색 관련 DTO
- **SearchRequestDto** - 검색 요청

### 10. JWT 관련 DTO
- **JwtLoginRequestDto** - JWT 로그인 요청
- **JwtResponseDto** - JWT 응답

### 11. 유틸리티
- **DtoConverter** - DTO 변환 유틸리티 클래스

## DTO 사용 패턴

### 요청 DTO (Request DTO)
```java
// 게시글 생성 요청
PostRequestDto request = new PostRequestDto();
request.setTitle("제목");
request.setContent("내용");
request.setCategory(Category.TECH);
request.setPublished(true);

// API 호출
POST /api/admin/posts
Body: request
```

### 응답 DTO (Response DTO)
```java
// 게시글 응답
PostResponseDto response = new PostResponseDto();
response.setId(1L);
response.setTitle("제목");
response.setContent("내용");
response.setCreatedAt(LocalDateTime.now());

// API 응답
{
  "success": true,
  "message": "성공",
  "data": response,
  "timestamp": "2024-01-01T00:00:00",
  "errorCode": null
}
```

### 공통 응답 래퍼
```java
// 성공 응답
ApiResponseDto<PostResponseDto> success = ApiResponseDto.success(postResponse, "게시글이 생성되었습니다");

// 실패 응답
ApiResponseDto<PostResponseDto> error = ApiResponseDto.error("게시글 생성에 실패했습니다", "POST_CREATE_FAILED");
```

## 보안 고려사항

### 1. 비밀번호 제외
- 모든 응답 DTO에서 비밀번호 필드를 제외
- UserResponseDto, AdminResponseDto는 비밀번호 없이 생성

### 2. 입력 검증
- @NotBlank, @Size, @Email 등 Bean Validation 어노테이션 사용
- 요청 DTO에 적절한 제약 조건 설정

### 3. 권한 확인
- CommentResponseDto에 isAuthor 필드 추가
- 현재 사용자가 작성자인지 여부 표시

## 변환 유틸리티

### DtoConverter 사용법
```java
@Autowired
private DtoConverter dtoConverter;

// 단일 변환
PostResponseDto response = dtoConverter.toPostResponseDto(postDto, "ko");

// 리스트 변환
List<PostResponseDto> responses = dtoConverter.toPostResponseDtoList(postDtos, "ko");

// 댓글 변환 (작성자 여부 포함)
CommentResponseDto commentResponse = dtoConverter.toCommentResponseDto(commentDto, currentUserId);
```

## 언어별 처리

### 다국어 지원
```java
// 한국어 게시글
PostResponseDto koPost = dtoConverter.toPostResponseDto(postDto, "ko");

// 일본어 게시글
PostResponseDto jpPost = dtoConverter.toPostResponseDto(postDto, "ja");
```

## API 엔드포인트별 DTO 매핑

### 게시글 API
- `GET /api/posts` → PostResponseDto[]
- `POST /api/admin/posts` → PostRequestDto (입력), PostResponseDto (출력)
- `PUT /api/admin/posts/{id}` → PostRequestDto (입력), PostResponseDto (출력)

### 사용자 API
- `POST /api/user/login` → JwtLoginRequestDto (입력), UserResponseDto + JWT (출력)
- `POST /api/user/register` → UserRegistrationDto (입력), UserResponseDto (출력)

### 댓글 API
- `GET /api/comments/kr/{postId}` → CommentResponseDto[]
- `POST /api/comments/kr/{postId}` → CommentRequestDto (입력), CommentResponseDto (출력)

### 좋아요 API
- `POST /api/like/kr/{postId}` → LikeResponseDto (출력)

## 마이그레이션 가이드

### 기존 코드에서 새로운 DTO 사용
```java
// 기존
@PostMapping("/posts")
public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
    // ...
}

// 새로운 방식
@PostMapping("/posts")
public ResponseEntity<ApiResponseDto<PostResponseDto>> createPost(@RequestBody PostRequestDto requestDto) {
    PostDto postDto = convertToPostDto(requestDto);
    PostDto savedPost = postService.savePost(postDto, lang);
    PostResponseDto response = dtoConverter.toPostResponseDto(savedPost, lang);
    return ResponseEntity.ok(ApiResponseDto.success(response, "게시글이 생성되었습니다"));
}
```

## 장점

1. **명확한 API 계약**: 요청과 응답이 명확히 분리됨
2. **보안 강화**: 민감한 정보(비밀번호)가 응답에서 제외됨
3. **유효성 검증**: Bean Validation을 통한 입력 검증
4. **다국어 지원**: 언어별 적절한 필드 선택
5. **일관된 응답**: ApiResponseDto를 통한 표준화된 응답 형식
6. **확장성**: 새로운 필드 추가가 용이함 