package com.example.hyuk_blog.util;

/**
 * 이메일 비밀번호 암호화 도구
 * Gmail 앱 비밀번호를 안전하게 암호화하여 설정 파일에 저장할 수 있도록 도와줍니다.
 */
public class EmailPasswordEncryptor {
    
    public static void main(String[] args) {
        System.out.println("=== 이메일 비밀번호 암호화 도구 ===");
        System.out.println("이 도구는 Gmail 앱 비밀번호를 암호화하여 설정 파일에 안전하게 저장할 수 있도록 도와줍니다.\n");
        
        // 예시 Gmail 앱 비밀번호 (실제로는 사용자가 입력해야 함)
        String exampleAppPassword = "lcocdtcskkcoydjw";
        
        System.out.println("1. Gmail 앱 비밀번호 암호화");
        System.out.println("원본 앱 비밀번호: " + exampleAppPassword);
        
        String encryptedPassword = SimpleEncryptor.encrypt(exampleAppPassword);
        System.out.println("암호화된 비밀번호: " + encryptedPassword);
        
        System.out.println("\n2. application.yml에 설정할 값:");
        System.out.println("spring:");
        System.out.println("  mail:");
        System.out.println("    password: ENC(" + encryptedPassword + ")");
        
        System.out.println("\n3. 환경 변수로 설정할 경우:");
        System.out.println("Windows PowerShell:");
        System.out.println("$env:MAIL_PASSWORD=\"ENC(" + encryptedPassword + ")\"");
        System.out.println("\nWindows CMD:");
        System.out.println("set MAIL_PASSWORD=ENC(" + encryptedPassword + ")");
        
        System.out.println("\n4. 복호화 테스트:");
        String decryptedPassword = SimpleEncryptor.decrypt(encryptedPassword);
        System.out.println("복호화된 비밀번호: " + decryptedPassword);
        System.out.println("검증 결과: " + (exampleAppPassword.equals(decryptedPassword) ? "성공" : "실패"));
        
        System.out.println("\n=== 사용 방법 ===");
        System.out.println("1. 실제 Gmail 앱 비밀번호로 위의 exampleAppPassword 변수를 변경하세요.");
        System.out.println("2. 이 클래스를 실행하여 암호화된 값을 생성하세요.");
        System.out.println("3. 생성된 ENC(암호화된값) 형태를 application.yml이나 환경 변수에 설정하세요.");
        System.out.println("4. 애플리케이션을 재시작하면 자동으로 복호화되어 사용됩니다.");
        
        System.out.println("\n=== 보안 주의사항 ===");
        System.out.println("- 암호화 키는 ENCRYPTION_KEY 환경 변수로 설정하세요.");
        System.out.println("- 암호화 키가 설정되지 않으면 기본 키가 사용됩니다.");
        System.out.println("- 운영 환경에서는 반드시 강력한 암호화 키를 사용하세요.");
    }
}
