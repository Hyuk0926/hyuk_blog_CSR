# 이메일 발송 기능 설정 가이드 (보안 강화 버전)

## 개요
문의 답변 시 자동으로 문의자에게 이메일을 발송하는 기능을 구현했습니다. **보안을 위해 이메일 비밀번호는 암호화하여 저장합니다.**

## 설정 방법

### 1. Gmail 앱 비밀번호 생성

1. **Gmail 계정에서 2단계 인증 활성화**
   - Gmail → 설정 → 보안 → 2단계 인증 활성화

2. **앱 비밀번호 생성**
   - Gmail → 설정 → 보안 → 앱 비밀번호
   - "앱 선택" → "기타(맞춤 이름)" → 이름 입력 (예: "Hyuk Blog")
   - 생성된 16자리 비밀번호 복사

### 2. 이메일 비밀번호 암호화

#### 방법 1: 암호화 도구 사용 (권장)
```bash
# 1. EmailPasswordEncryptor.java 파일에서 실제 앱 비밀번호로 변경
# 2. 암호화 도구 실행
cd hyuk_blog
java -cp build/classes/java/main com.example.hyuk_blog.util.EmailPasswordEncryptor
```

#### 방법 2: 수동 암호화
```java
// SimpleEncryptor 클래스의 main 메서드 사용
String appPassword = "your-16-digit-app-password";
String encrypted = SimpleEncryptor.encrypt(appPassword);
System.out.println("ENC(" + encrypted + ")");
```

### 3. 환경 변수 설정

#### Windows PowerShell에서:
```powershell
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="ENC(your-encrypted-app-password)"
```

#### Windows CMD에서:
```cmd
set MAIL_USERNAME=your-email@gmail.com
set MAIL_PASSWORD=ENC(your-encrypted-app-password)
```

#### Linux/Mac에서:
```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=ENC(your-encrypted-app-password)
```

### 4. 시스템 환경 변수로 영구 설정 (Windows)

1. **시스템 속성** → **환경 변수**
2. **새로 만들기** 클릭
3. 변수 이름: `MAIL_USERNAME`, 값: `your-email@gmail.com`
4. 변수 이름: `MAIL_PASSWORD`, 값: `ENC(your-encrypted-app-password)`

### 5. 암호화 키 설정 (선택사항)

기본 암호화 키 대신 사용자 정의 키를 사용하려면:

#### Windows PowerShell에서:
```powershell
$env:ENCRYPTION_KEY="your-custom-encryption-key"
```

#### Windows CMD에서:
```cmd
set ENCRYPTION_KEY=your-custom-encryption-key
```

## 테스트 방법

### 1. 백엔드 서버 재시작
```cmd
cd hyuk_blog
.\gradlew bootRun
```

### 2. 이메일 테스트 API 호출
```bash
curl -X POST http://localhost:9090/api/admin/email/test \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"toEmail": "test@example.com"}'
```

### 3. 문의 답변 테스트
1. Contact 페이지에서 테스트 문의 보내기
2. 관리자로 로그인하여 문의 관리 페이지 접속
3. 문의에 답변 작성
4. 문의자의 이메일로 답변이 자동 발송되는지 확인

## 이메일 내용 예시

```
안녕하세요,

문의해주신 내용에 대한 답변을 드립니다.

=== 문의 내용 ===
제목: [문의자 입력한 제목]

=== 답변 내용 ===
[관리자가 작성한 답변 내용]

추가 문의사항이 있으시면 언제든지 연락해 주세요.

감사합니다.
관리자 드림
---
이 메일은 자동으로 발송되었습니다.
```

## 보안 개선사항

### 이전 방식 (취약)
```yaml
spring:
  mail:
    password: ${MAIL_PASSWORD:your-app-password}  # 평문 저장
```

### 현재 방식 (안전)
```yaml
spring:
  mail:
    password: ${MAIL_PASSWORD:ENC(encrypted-password)}  # 암호화 저장
```

### 보안 특징
1. **AES 암호화**: 256비트 AES 암호화 사용
2. **환경 변수 지원**: 시스템 환경 변수에서 암호화된 값 로드
3. **자동 복호화**: 애플리케이션 시작 시 자동으로 복호화
4. **키 관리**: 사용자 정의 암호화 키 지원

## 주의사항

1. **Gmail 앱 비밀번호 사용 필수**: 일반 Gmail 비밀번호는 사용할 수 없습니다.
2. **2단계 인증 필수**: 앱 비밀번호 생성 전에 2단계 인증을 활성화해야 합니다.
3. **암호화 필수**: 보안을 위해 반드시 암호화된 비밀번호를 사용하세요.
4. **환경 변수 설정**: 서버 재시작 시 환경 변수가 유지되도록 영구 설정을 권장합니다.
5. **이메일 발송 실패 시**: 답변은 정상적으로 저장되며, 이메일 발송 실패는 로그에 기록됩니다.

## 문제 해결

### 이메일 발송 실패 시 확인사항:
1. Gmail 2단계 인증이 활성화되어 있는지 확인
2. 앱 비밀번호가 올바른지 확인
3. 암호화된 비밀번호가 올바르게 설정되어 있는지 확인
4. 환경 변수가 올바르게 설정되어 있는지 확인
5. 방화벽이나 보안 소프트웨어가 SMTP 연결을 차단하지 않는지 확인

### 로그 확인:
```bash
# 이메일 발송 성공 로그
문의 답변 이메일 발송 성공: user@example.com

# 이메일 발송 실패 로그
문의 답변 이메일 발송 실패: user@example.com - [오류 메시지]
```

### 암호화 관련 문제:
1. 암호화 키가 올바르게 설정되어 있는지 확인
2. 암호화된 값이 올바른 형식인지 확인 (ENC(...) 형태)
3. 복호화 테스트를 통해 암호화/복호화가 정상 작동하는지 확인
