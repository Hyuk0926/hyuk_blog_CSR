# DelegatingPasswordEncoder 구현 가이드

## 개요

이 프로젝트는 Spring Security의 `DelegatingPasswordEncoder`를 사용하여 여러 암호화 방식을 동시에 지원하는 시스템을 구현했습니다. 이를 통해 기존 SHA-256 비밀번호를 가진 사용자들이 로그인할 수 있으면서도, 새로운 사용자들은 더 안전한 BCrypt 방식으로 비밀번호가 저장됩니다.

## 구현된 기능

### 1. DelegatingPasswordEncoder 설정

**파일**: `SecurityConfig.java`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    String idForEncode = "bcrypt";
    
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put(idForEncode, new BCryptPasswordEncoder());
    encoders.put("sha256", new Sha256PasswordEncoder());
    
    DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
    passwordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
    
    return passwordEncoder;
}
```

### 2. 커스텀 SHA-256 인코더

기존 SHA-256 해시를 지원하기 위한 커스텀 인코더를 구현했습니다.

```java
public static class Sha256PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        // SHA-256 해시 생성
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 비밀번호 검증
    }
}
```

### 3. 자동 비밀번호 업그레이드

로그인 시 기존 비밀번호가 구식 방식이라면 자동으로 새로운 방식으로 업그레이드됩니다.

```java
if (passwordEncoder.upgradeEncoding(user.getPassword())) {
    user.updatePassword(passwordEncoder.encode(loginRequest.getPassword()));
    userRepository.save(user);
    log.info("사용자 '{}'의 비밀번호를 새로운 방식으로 업그레이드했습니다.", user.getUsername());
}
```

### 4. 데이터베이스 마이그레이션

기존 SHA-256 비밀번호에 `{sha256}` 접두사를 자동으로 추가합니다.

## 지원하는 비밀번호 형식

1. **BCrypt**: `$2a$10$...` (새로운 기본 방식)
2. **SHA-256 with prefix**: `{sha256}64자리해시` (기존 방식)
3. **BCrypt with prefix**: `{bcrypt}$2a$10$...` (명시적 BCrypt)

## 작동 방식

### 로그인 프로세스

1. 사용자가 로그인 시도
2. `DelegatingPasswordEncoder`가 저장된 비밀번호의 접두사를 확인
3. 접두사에 따라 적절한 인코더로 검증 위임
4. 검증 성공 시, 비밀번호가 구식 방식이면 자동 업그레이드
5. 로그인 성공

### 비밀번호 업그레이드

- **SHA-256 → BCrypt**: 로그인 시 자동 변환
- **평문 → BCrypt**: 로그인 시 자동 변환
- **BCrypt → BCrypt**: 이미 최신 방식이므로 변환 없음

## API 엔드포인트

### 로그인
```
POST /api/auth/login
Content-Type: application/json

{
    "username": "사용자명",
    "password": "비밀번호"
}
```

### 비밀번호 테스트 (개발용)
```
POST /api/auth/test-password
Content-Type: application/json

{
    "password": "원본비밀번호",
    "encodedPassword": "암호화된비밀번호"
}
```

## 마이그레이션 과정

### 1단계: 애플리케이션 시작 시
- 기존 SHA-256 비밀번호에 `{sha256}` 접두사 자동 추가
- 로그: "비밀번호 마이그레이션 완료: 사용자 X명, 관리자 Y명"

### 2단계: 사용자 로그인 시
- 구식 비밀번호 자동 업그레이드
- 로그: "사용자 'username'의 비밀번호를 새로운 방식으로 업그레이드했습니다."

## 보안 고려사항

1. **점진적 업그레이드**: 사용자 불편 없이 보안 강화
2. **다중 지원**: 기존 시스템과의 호환성 유지
3. **자동 마이그레이션**: 관리자 개입 없이 자동 처리
4. **로깅**: 모든 비밀번호 변경 사항 추적

## 테스트 방법

### 1. 기존 SHA-256 비밀번호로 로그인
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"기존사용자","password":"기존비밀번호"}'
```

### 2. 새 사용자 회원가입 (BCrypt 자동 적용)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"새사용자","password":"새비밀번호","nickname":"닉네임","email":"test@example.com"}'
```

### 3. 비밀번호 인코더 테스트
```bash
curl -X POST http://localhost:8080/api/auth/test-password \
  -H "Content-Type: application/json" \
  -d '{"password":"test123","encodedPassword":"{sha256}a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"}'
```

## 로그 확인

애플리케이션 로그에서 다음 메시지들을 확인할 수 있습니다:

- `비밀번호 마이그레이션 시작...`
- `사용자 'username'의 비밀번호에 {sha256} 접두사 추가`
- `사용자 'username'의 비밀번호를 새로운 방식으로 업그레이드했습니다.`
- `비밀번호 마이그레이션 완료: 사용자 X명, 관리자 Y명`

## 장점

1. **호환성**: 기존 사용자 계정 그대로 사용 가능
2. **보안**: 새로운 계정은 더 안전한 BCrypt 사용
3. **자동화**: 관리자 개입 없이 자동 마이그레이션
4. **투명성**: 사용자는 변경 사항을 인지하지 못함
5. **확장성**: 새로운 암호화 방식 쉽게 추가 가능

## 주의사항

1. **일회성 마이그레이션**: 접두사 추가는 한 번만 실행됨
2. **백업 권장**: 마이그레이션 전 데이터베이스 백업
3. **테스트 환경**: 운영 환경 적용 전 충분한 테스트 필요
4. **모니터링**: 로그를 통한 마이그레이션 진행 상황 확인

## 결론

`DelegatingPasswordEncoder`를 사용한 이 구현은 기존 시스템의 안정성을 유지하면서도 보안을 점진적으로 강화할 수 있는 모범적인 방법입니다. 사용자 경험을 해치지 않으면서도 시스템 전체의 보안 수준을 높일 수 있습니다.
