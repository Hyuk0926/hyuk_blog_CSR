# 🔒 보안 가이드

## 환경변수 설정

### 1. Gmail 앱 비밀번호 암호화
```bash
# Windows
set GMAIL_APP_PASSWORD=your_gmail_app_password

# Linux/Mac
export GMAIL_APP_PASSWORD=your_gmail_app_password
```

### 2. 관리자 계정 비밀번호
```bash
# Windows
set ADMIN_PASSWORD=your_admin_password
set JP_ADMIN_PASSWORD=your_jp_admin_password

# Linux/Mac
export ADMIN_PASSWORD=your_admin_password
export JP_ADMIN_PASSWORD=your_jp_admin_password
```

### 3. 데모 사용자 계정 (비밀번호 재설정 테스트용)
```bash
# Windows
set DEMO_USER_USERNAME=your_demo_username
set DEMO_USER_PASSWORD=your_demo_password
set DEMO_USER_EMAIL=your_demo_email@example.com
set DEMO_USER_NICKNAME=your_demo_nickname

# Linux/Mac
export DEMO_USER_USERNAME=your_demo_username
export DEMO_USER_PASSWORD=your_demo_password
export DEMO_USER_EMAIL=your_demo_email@example.com
export DEMO_USER_NICKNAME=your_demo_nickname
```

### 4. 프론트엔드 API 키 (Vue.js)
```bash
# frontend/.env.local 파일 생성
VUE_APP_TINYMCE_API_KEY=your_tinymce_api_key_here
VUE_APP_API_URL=http://localhost:9090
VUE_APP_FRONTEND_URL=http://localhost:3000
```

## 보안 주의사항

### ✅ 권장사항
- 모든 비밀번호는 환경변수로 관리
- 운영 환경에서는 반드시 환경변수 설정
- 코드에 비밀번호를 하드코딩하지 않음
- 정기적인 비밀번호 변경

### ❌ 금지사항
- 코드에 실제 비밀번호 작성
- 코드에 API 키 하드코딩
- Git에 환경변수 파일 커밋
- 기본 비밀번호 사용 (운영 환경)
- 비밀번호를 로그에 출력

## 환경별 설정

### 개발 환경 (dev)
- 기본 비밀번호 허용 (경고 로그 출력)
- 테스트 사용자 자동 생성

### 운영 환경 (prod)
- 환경변수 필수 설정
- 기본 비밀번호 사용 시 오류 발생
- 테스트 사용자 생성 안함

## 암호화된 비밀번호 생성 방법

1. 환경변수 설정
2. SimpleEncryptor.main() 실행
3. 생성된 암호화 값 복사
4. application.yml에 ENC(암호화값) 형태로 저장
5. 환경변수 삭제

```java
// 예시
String encrypted = SimpleEncryptor.encrypt("your_password");
// 결과: ENC(암호화된_값)
```
