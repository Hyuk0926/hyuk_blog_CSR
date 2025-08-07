# Hyuk Blog REST API 엔드포인트

## 기본 정보
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **인증**: JWT Bearer Token (Authorization 헤더)

## 인증 관련 API

### 사용자 인증
- `POST /api/user/login` - 사용자 로그인
- `POST /api/user/register` - 사용자 회원가입
- `POST /api/user/logout` - 사용자 로그아웃
- `GET /api/user/profile` - 사용자 정보 조회
- `PUT /api/user/profile` - 사용자 정보 수정
- `POST /api/user/check-username` - 사용자명 중복 확인
- `POST /api/user/check-nickname` - 닉네임 중복 확인
- `POST /api/user/check-email` - 이메일 중복 확인

### 관리자 인증
- `POST /api/admin/auth/login` - 관리자 로그인
- `POST /api/admin/auth/logout` - 관리자 로그아웃
- `GET /api/admin/auth/profile` - 관리자 정보 조회
- `PUT /api/admin/auth/profile` - 관리자 정보 수정

### 관리자 관리 API
- `GET /api/admin` - 모든 관리자 조회
- `GET /api/admin/{id}` - 특정 관리자 조회
- `POST /api/admin` - 새 관리자 생성
- `PUT /api/admin/{id}` - 관리자 정보 업데이트
- `DELETE /api/admin/{id}` - 관리자 삭제 (비활성화)
- `GET /api/admin/check-username` - 사용자명 중복 확인

### JWT 토큰
- `POST /api/auth/validate` - JWT 토큰 검증
- `GET /api/auth/info` - JWT 토큰 정보 조회

## 게시글 관련 API

### 공개 API
- `GET /api/posts` - 모든 게시글 조회
- `GET /api/posts/category/{category}` - 카테고리별 게시글 조회
- `GET /api/posts/{id}` - 게시글 상세 조회
- `GET /api/posts/search` - 게시글 검색
- `GET /api/resume` - 이력서 정보 조회 (언어별)
- `POST /api/inquiry` - 문의 등록

### 관리자 전용 API
- `GET /api/admin/dashboard` - 관리자 대시보드 데이터
- `POST /api/admin/posts` - 게시글 생성
- `PUT /api/admin/posts/{id}` - 게시글 수정
- `DELETE /api/admin/posts/{id}` - 게시글 삭제
- `GET /api/admin/posts/{id}` - 게시글 상세 조회 (관리자용)
- `PUT /api/resume` - 이력서 정보 업데이트 (관리자용)

## 댓글 관련 API

### 댓글 관리
- `GET /api/comments/kr/{postId}` - 한국어 게시글 댓글 조회
- `GET /api/comments/jp/{postId}` - 일본어 게시글 댓글 조회
- `POST /api/comments/kr/{postId}` - 한국어 게시글 댓글 작성
- `POST /api/comments/jp/{postId}` - 일본어 게시글 댓글 작성
- `PUT /api/comments/{commentId}` - 댓글 수정
- `DELETE /api/comments/{commentId}` - 댓글 삭제

## 좋아요 관련 API

### 좋아요 관리
- `POST /api/like/kr/{postId}` - 한국어 게시글 좋아요 토글
- `POST /api/like/jp/{postId}` - 일본어 게시글 좋아요 토글
- `GET /api/like/kr/{postId}/status` - 한국어 게시글 좋아요 상태 조회
- `GET /api/like/jp/{postId}/status` - 일본어 게시글 좋아요 상태 조회

## 방문자 통계 API

### 방문자 통계
- `POST /api/visitor/increase` - 방문자 수 증가
- `GET /api/visitor/stats/daily` - 일별 방문자 통계
- `GET /api/visitor/stats/monthly` - 월별 방문자 통계
- `GET /api/visitor/today` - 오늘 방문자 수
- `GET /api/visitor/month` - 이번 달 방문자 수
- `GET /api/visitor/stats/summary` - 전체 방문자 통계 요약

## 관리자 문의 관리 API

### 문의 관리
- `GET /api/admin/inquiries` - 문의 목록 조회
- `GET /api/admin/inquiries/recent` - 최근 문의 조회
- `POST /api/admin/inquiries/read` - 모든 문의 읽음 처리
- `POST /api/admin/inquiries/{id}/read` - 개별 문의 읽음 처리
- `DELETE /api/admin/inquiries/{id}` - 문의 삭제

## 요청/응답 예시

### 사용자 로그인
```json
// 요청
POST /api/user/login
{
  "username": "user123",
  "password": "password123"
}

// 응답
{
  "success": true,
  "message": "로그인에 성공했습니다.",
  "user": {
    "id": 1,
    "username": "user123",
    "nickname": "사용자",
    "email": "user@example.com"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 게시글 조회
```json
// 요청
GET /api/posts?lang=ko

// 응답
{
  "posts": [
    {
      "id": 1,
      "title": "첫 번째 게시글",
      "content": "게시글 내용...",
      "category": "TECH",
      "published": true,
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "categories": ["TECH", "LIFE", "PROJECT"],
  "lang": "ko"
}
```

### 댓글 작성
```json
// 요청
POST /api/comments/kr/1
{
  "content": "좋은 게시글이네요!"
}

// 응답
{
  "success": true,
  "message": "댓글이 작성되었습니다.",
  "comment": {
    "id": 1,
    "content": "좋은 게시글이네요!",
    "nickname": "사용자",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 관리자 생성
```json
// 요청
POST /api/admin
{
  "username": "newadmin",
  "password": "password123",
  "name": "새 관리자",
  "email": "newadmin@example.com"
}

// 응답
{
  "success": true,
  "message": "관리자가 성공적으로 생성되었습니다.",
  "data": {
    "id": 2,
    "username": "newadmin",
    "name": "새 관리자",
    "email": "newadmin@example.com",
    "active": true,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 관리자 정보 업데이트
```json
// 요청
PUT /api/admin/2
{
  "name": "수정된 관리자",
  "email": "updated@example.com"
}

// 응답
{
  "success": true,
  "message": "관리자 정보가 성공적으로 업데이트되었습니다.",
  "data": {
    "id": 2,
    "username": "newadmin",
    "name": "수정된 관리자",
    "email": "updated@example.com",
    "active": true,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 이력서 조회 (언어별)
```json
// 요청
GET /api/resume?lang=ko

// 응답
{
  "success": true,
  "message": "이력서 조회에 성공했습니다.",
  "data": {
    "name": "홍길동",
    "email": "hong@example.com",
    "phone": "010-1234-5678",
    "photoUrl": "/img/profile.jpg",
    "birth": "1995-01-01",
    "address": "서울시 강남구",
    "skills": "Java, Spring, Vue.js",
    "introduction": "안녕하세요. 개발자 홍길동입니다.",
    "studentLife": "대학 시절 다양한 프로젝트를 통해...",
    "strengthsWeaknesses": "장점은 꼼꼼함이고, 단점은 완벽주의입니다.",
    "effortExperience": "알고리즘 공부를 위해 매일 2시간씩...",
    "japanItMotivation": "일본 IT 업계에서 일하고 싶은 이유는...",
    "futurePlan": "앞으로 5년간 일본에서 개발자로...",
    "educations": [
      {
        "school": "서울대학교",
        "degree": "컴퓨터공학과",
        "period": "2014-2018"
      }
    ]
  }
}
```

### 이력서 업데이트 (관리자용)
```json
// 요청
PUT /api/resume
{
  "nameKo": "홍길동",
  "nameJa": "ホン・ギルドン",
  "email": "hong@example.com",
  "phone": "010-1234-5678",
  "photoUrl": "/img/profile.jpg",
  "birth": "1995-01-01",
  "addressKo": "서울시 강남구",
  "addressJa": "ソウル市江南区",
  "skills": "Java, Spring, Vue.js",
  "introductionKo": "안녕하세요. 개발자 홍길동입니다.",
  "studentLifeKo": "대학 시절 다양한 프로젝트를 통해...",
  "strengthsWeaknessesKo": "장점은 꼼꼼함이고, 단점은 완벽주의입니다.",
  "effortExperienceKo": "알고리즘 공부를 위해 매일 2시간씩...",
  "japanItMotivationKo": "일본 IT 업계에서 일하고 싶은 이유는...",
  "futurePlanKo": "앞으로 5년간 일본에서 개발자로...",
  "studentLifeJa": "大学時代、様々なプロジェクトを通じて...",
  "strengthsWeaknessesJa": "長所は几帳面さで、短所は完璧主義です。",
  "effortExperienceJa": "アルゴリズムの勉強のために毎日2時間...",
  "japanItMotivationJa": "日本のIT業界で働きたい理由は...",
  "futurePlanJa": "今後5年間、日本で開発者として...",
  "educations": [
    {
      "schoolKo": "서울대학교",
      "schoolJa": "ソウル大学校",
      "degreeKo": "컴퓨터공학과",
      "degreeJa": "コンピュータ工学科",
      "period": "2014-2018"
    }
  ]
}

// 응답
{
  "success": true,
  "message": "이력서가 성공적으로 업데이트되었습니다.",
  "data": {
    "name": "홍길동",
    "email": "hong@example.com",
    "phone": "010-1234-5678",
    "photoUrl": "/img/profile.jpg",
    "birth": "1995-01-01",
    "address": "서울시 강남구",
    "skills": "Java, Spring, Vue.js",
    "introduction": "안녕하세요. 개발자 홍길동입니다.",
    "studentLife": "대학 시절 다양한 프로젝트를 통해...",
    "strengthsWeaknesses": "장점은 꼼꼼함이고, 단점은 완벽주의입니다.",
    "effortExperience": "알고리즘 공부를 위해 매일 2시간씩...",
    "japanItMotivation": "일본 IT 업계에서 일하고 싶은 이유는...",
    "futurePlan": "앞으로 5년간 일본에서 개발자로...",
    "educations": [
      {
        "school": "서울대학교",
        "degree": "컴퓨터공학과",
        "period": "2014-2018"
      }
    ]
  }
}
```

## 에러 응답 형식

```json
{
  "success": false,
  "message": "에러 메시지"
}
```

## HTTP 상태 코드

- `200` - 성공
- `201` - 생성 성공
- `400` - 잘못된 요청
- `401` - 인증 실패
- `403` - 권한 없음
- `404` - 리소스 없음
- `500` - 서버 오류

## 프론트엔드 연동

프론트엔드에서는 `frontend/src/services/api.js` 파일을 통해 API를 호출할 수 있습니다.

```javascript
import { authAPI, postAPI, commentAPI } from '@/services/api';

// 사용자 로그인
const login = async (credentials) => {
  try {
    const response = await authAPI.userLogin(credentials);
    if (response.data.success) {
      // 로그인 성공 처리
    }
  } catch (error) {
    // 에러 처리
  }
};

// 게시글 조회
const getPosts = async (lang = 'ko') => {
  try {
    const response = await postAPI.getAllPosts(lang);
    return response.data.posts;
  } catch (error) {
    // 에러 처리
  }
};
```

## CORS 설정

모든 API 엔드포인트는 `@CrossOrigin(origins = "*")`로 설정되어 있어 프론트엔드에서 자유롭게 호출할 수 있습니다.

## Swagger 문서

API 문서는 Swagger UI를 통해 확인할 수 있습니다:
- **URL**: `http://localhost:8080/swagger-ui.html` 