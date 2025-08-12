package com.example.hyuk_blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Inquiry {
    @Id
    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
    
    // 답변 관련 필드
    private boolean replied = false;
    private String replyMessage;
    private LocalDateTime repliedAt;
    private String repliedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // 답변 관련 getter/setter
    public boolean isReplied() { return replied; }
    public void setReplied(boolean replied) { this.replied = replied; }
    
    public String getReplyMessage() { return replyMessage; }
    public void setReplyMessage(String replyMessage) { this.replyMessage = replyMessage; }
    
    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }
    
    public String getRepliedBy() { return repliedBy; }
    public void setRepliedBy(String repliedBy) { this.repliedBy = repliedBy; }
} 