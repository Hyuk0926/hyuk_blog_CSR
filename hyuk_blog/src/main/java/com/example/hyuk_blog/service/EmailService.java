package com.example.hyuk_blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[문의 답변] " + inquirySubject);
            
            String emailContent = buildReplyEmailContent(inquirySubject, replyMessage, adminName);
            message.setText(emailContent);
            
            mailSender.send(message);
            log.info("문의 답변 이메일 발송 성공: {}", toEmail);
            
        } catch (Exception e) {
            log.error("문의 답변 이메일 발송 실패: {} - {}", toEmail, e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 답변 이메일 내용을 구성합니다.
     */
    private String buildReplyEmailContent(String inquirySubject, String replyMessage, String adminName) {
        StringBuilder content = new StringBuilder();
        content.append("안녕하세요,\n\n");
        content.append("문의해주신 내용에 대한 답변을 드립니다.\n\n");
        content.append("=== 문의 내용 ===\n");
        content.append("제목: ").append(inquirySubject).append("\n\n");
        content.append("=== 답변 내용 ===\n");
        content.append(replyMessage).append("\n\n");
        content.append("추가 문의사항이 있으시면 언제든지 연락해 주세요.\n\n");
        content.append("감사합니다.\n");
        content.append(adminName).append(" 드림\n");
        content.append("---\n");
        content.append("이 메일은 자동으로 발송되었습니다.");
        
        return content.toString();
    }

    /**
     * 이메일 발송 테스트
     */
    public void sendTestEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[테스트] 이메일 발송 테스트");
            message.setText("이메일 발송 기능이 정상적으로 작동합니다.");
            
            mailSender.send(message);
            log.info("테스트 이메일 발송 성공: {}", toEmail);
            
        } catch (Exception e) {
            log.error("테스트 이메일 발송 실패: {} - {}", toEmail, e.getMessage());
            throw new RuntimeException("테스트 이메일 발송에 실패했습니다.", e);
        }
    }
}
