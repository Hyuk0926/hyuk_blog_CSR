package com.example.hyuk_blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 문의 답변 이메일을 발송합니다.
     */
    public void sendInquiryReplyEmail(String toEmail, String inquirySubject, String replyMessage, String adminName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[문의 답변] " + inquirySubject);
            
            String htmlContent = buildReplyEmailHtmlContent(inquirySubject, replyMessage, adminName);
            helper.setText(htmlContent, true); // true = HTML 형식
            
            mailSender.send(message);
            log.info("문의 답변 이메일 발송 성공: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("문의 답변 이메일 발송 실패: {} - {}", toEmail, e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * HTML 형식의 답변 이메일 내용을 구성합니다.
     */
    private String buildReplyEmailHtmlContent(String inquirySubject, String replyMessage, String adminName) {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>문의 답변</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f8f9fa;
                    }
                    .email-container {
                        background-color: #ffffff;
                        border-radius: 10px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 30px 20px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 24px;
                        font-weight: 300;
                    }
                    .content {
                        padding: 30px 20px;
                    }
                    .greeting {
                        font-size: 18px;
                        color: #2c3e50;
                        margin-bottom: 20px;
                    }
                    .section {
                        margin-bottom: 25px;
                        padding: 20px;
                        background-color: #f8f9fa;
                        border-radius: 8px;
                        border-left: 4px solid #667eea;
                    }
                    .section-title {
                        font-weight: 600;
                        color: #2c3e50;
                        margin-bottom: 10px;
                        font-size: 16px;
                    }
                    .inquiry-subject {
                        background-color: #e3f2fd;
                        padding: 15px;
                        border-radius: 6px;
                        border-left: 4px solid #2196f3;
                        margin-bottom: 15px;
                    }
                    .reply-content {
                        background-color: #f1f8e9;
                        padding: 15px;
                        border-radius: 6px;
                        border-left: 4px solid #4caf50;
                        white-space: pre-wrap;
                        line-height: 1.8;
                    }
                    .footer {
                        background-color: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        border-top: 1px solid #e9ecef;
                    }
                    .signature {
                        margin-top: 20px;
                        padding-top: 20px;
                        border-top: 1px solid #e9ecef;
                    }
                    .admin-name {
                        font-weight: 600;
                        color: #667eea;
                    }
                    .auto-notice {
                        font-size: 12px;
                        color: #6c757d;
                        margin-top: 15px;
                        padding-top: 15px;
                        border-top: 1px solid #e9ecef;
                    }
                    .contact-info {
                        margin-top: 15px;
                        padding: 15px;
                        background-color: #fff3cd;
                        border-radius: 6px;
                        border-left: 4px solid #ffc107;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <h1>📧 문의 답변</h1>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">
                            안녕하세요! 👋
                        </div>
                        
                        <p>문의해주신 내용에 대한 답변을 드립니다.</p>
                        
                        <div class="section">
                            <div class="section-title">📝 문의 내용</div>
                            <div class="inquiry-subject">
                                <strong>제목:</strong> %s
                            </div>
                        </div>
                        
                        <div class="section">
                            <div class="section-title">💬 답변 내용</div>
                            <div class="reply-content">%s</div>
                        </div>
                        
                        <div class="contact-info">
                            <strong>💡 추가 문의사항이 있으시면 언제든지 연락해 주세요!</strong>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="signature">
                            감사합니다.<br>
                            <span class="admin-name">%s</span> 드림
                        </div>
                        
                        <div class="auto-notice">
                            이 메일은 자동으로 발송되었습니다.
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(inquirySubject, replyMessage, adminName);
    }

    /**
     * 비밀번호 재설정 이메일을 발송합니다.
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[비밀번호 재설정] Hyuk Blog");
            
            String htmlContent = buildPasswordResetEmailHtmlContent(resetUrl);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("비밀번호 재설정 이메일 발송 성공: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("비밀번호 재설정 이메일 발송 실패: {} - {}", toEmail, e.getMessage());
            throw new RuntimeException("비밀번호 재설정 이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * HTML 형식의 비밀번호 재설정 이메일 내용을 구성합니다.
     */
    private String buildPasswordResetEmailHtmlContent(String resetUrl) {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>비밀번호 재설정</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        line-height: 1.6;
                        color: #333333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f5f5f5;
                    }
                    .email-container {
                        background-color: #ffffff;
                        border: 1px solid #e0e0e0;
                        border-radius: 4px;
                        overflow: hidden;
                    }
                    .header {
                        background-color: #333333;
                        color: #ffffff;
                        padding: 24px 20px;
                        text-align: center;
                        border-bottom: 1px solid #e0e0e0;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 20px;
                        font-weight: 500;
                        letter-spacing: 0.5px;
                    }
                    .content {
                        padding: 32px 24px;
                    }
                    .greeting {
                        font-size: 16px;
                        color: #333333;
                        margin-bottom: 24px;
                        font-weight: 500;
                    }
                    .message {
                        color: #555555;
                        margin-bottom: 24px;
                    }
                    .warning-box {
                        background-color: #f8f8f8;
                        border: 1px solid #d0d0d0;
                        border-radius: 4px;
                        padding: 16px;
                        margin: 24px 0;
                        border-left: 3px solid #666666;
                    }
                    .warning-box strong {
                        color: #333333;
                    }
                    .reset-button {
                        display: inline-block;
                        background-color: #333333;
                        color: #ffffff;
                        padding: 12px 24px;
                        text-decoration: none;
                        border-radius: 4px;
                        font-weight: 500;
                        margin: 24px 0;
                        text-align: center;
                        border: none;
                        font-size: 14px;
                    }
                    .reset-button:hover {
                        background-color: #555555;
                    }
                    .security-info {
                        background-color: #f8f8f8;
                        border: 1px solid #d0d0d0;
                        border-radius: 4px;
                        padding: 16px;
                        margin: 24px 0;
                        border-left: 3px solid #666666;
                    }
                    .security-info strong {
                        color: #333333;
                    }
                    .security-info ul {
                        margin: 8px 0;
                        padding-left: 20px;
                    }
                    .security-info li {
                        color: #555555;
                        margin-bottom: 4px;
                    }
                    .link-text {
                        word-break: break-all;
                        color: #666666;
                        font-size: 12px;
                        background-color: #f8f8f8;
                        padding: 12px;
                        border-radius: 4px;
                        border: 1px solid #e0e0e0;
                        margin-top: 16px;
                    }
                    .footer {
                        background-color: #f8f8f8;
                        padding: 20px 24px;
                        text-align: center;
                        border-top: 1px solid #e0e0e0;
                    }
                    .auto-notice {
                        font-size: 12px;
                        color: #888888;
                        line-height: 1.4;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <h1>비밀번호 재설정</h1>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">
                            안녕하세요,
                        </div>
                        
                        <div class="message">
                            Hyuk Blog에서 비밀번호 재설정 요청을 받았습니다.
                        </div>
                        
                        <div class="warning-box">
                            <strong>주의사항</strong><br>
                            본인이 요청하지 않은 경우 이 이메일을 무시하시면 됩니다.
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="reset-button">
                                비밀번호 재설정하기
                            </a>
                        </div>
                        
                        <div class="security-info">
                            <strong>보안 안내</strong>
                            <ul>
                                <li>이 링크는 24시간 동안만 유효합니다</li>
                                <li>한 번 사용하면 더 이상 사용할 수 없습니다</li>
                                <li>안전한 환경에서 비밀번호를 변경하세요</li>
                            </ul>
                        </div>
                        
                        <p style="color: #555555; font-size: 14px; margin-bottom: 8px;">
                            버튼이 작동하지 않는 경우, 아래 링크를 복사하여 브라우저에 붙여넣으세요:
                        </p>
                        <div class="link-text">%s</div>
                    </div>
                    
                    <div class="footer">
                        <div class="auto-notice">
                            이 메일은 자동으로 발송되었습니다.<br>
                            문의사항이 있으시면 관리자에게 연락해 주세요.
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.replace("%s", resetUrl);
    }

    /**
     * 이메일 발송 테스트
     */
    public void sendTestEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[테스트] 이메일 발송 테스트");
            
            String testHtmlContent = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        .success { color: #28a745; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <h2>✅ 이메일 발송 테스트</h2>
                    <p class="success">이메일 발송 기능이 정상적으로 작동합니다!</p>
                    <p>발송 시간: %s</p>
                </body>
                </html>
                """.formatted(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            helper.setText(testHtmlContent, true);
            
            mailSender.send(message);
            log.info("테스트 이메일 발송 성공: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("테스트 이메일 발송 실패: {} - {}", toEmail, e.getMessage());
            throw new RuntimeException("테스트 이메일 발송에 실패했습니다.", e);
        }
    }
}
