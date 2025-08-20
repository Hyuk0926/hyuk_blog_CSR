package com.example.hyuk_blog.config;

import com.example.hyuk_blog.util.SimpleEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    
    @Value("${spring.mail.host}")
    private String host;
    
    @Value("${spring.mail.port}")
    private int port;
    
    @Value("${spring.mail.username}")
    private String username;
    
    @Value("${spring.mail.password}")
    private String password;
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        
        // 디버깅: 원본 비밀번호 값 확인
        System.out.println("=== EmailConfig Debug ===");
        System.out.println("Original password value: '" + password + "'");
        System.out.println("Password length: " + password.length());
        
        // 암호화된 비밀번호 복호화
        String decryptedPassword = password;
        if (password.startsWith("ENC(") && password.endsWith(")")) {
            String encryptedValue = password.substring(4, password.length() - 1);
            System.out.println("Encrypted value: '" + encryptedValue + "'");
            System.out.println("Encrypted value length: " + encryptedValue.length());
            
            try {
                decryptedPassword = SimpleEncryptor.decrypt(encryptedValue);
                System.out.println("Decryption successful: " + decryptedPassword);
            } catch (Exception e) {
                System.err.println("Decryption failed: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Email password decryption failed", e);
            }
        }
        mailSender.setPassword(decryptedPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.connectiontimeout", "5000");
        
        return mailSender;
    }
}
