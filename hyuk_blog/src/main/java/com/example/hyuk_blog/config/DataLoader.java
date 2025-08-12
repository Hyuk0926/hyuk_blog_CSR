package com.example.hyuk_blog.config;

import com.example.hyuk_blog.entity.Admin;
import com.example.hyuk_blog.entity.Inquiry;
import com.example.hyuk_blog.entity.Resume;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.repository.AdminRepository;
import com.example.hyuk_blog.repository.InquiryRepository;
import com.example.hyuk_blog.repository.ResumeRepository;
import com.example.hyuk_blog.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private AdminRepository adminRepository;
    @Autowired private InquiryRepository inquiryRepository;
    @Autowired private ResumeRepository resumeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        
        // 관리자 계정 처리
        if (adminRepository.count() == 0) {
            // 관리자 계정이 없으면 새로 생성
            createInitialAdmin();
            createJapaneseAdmin();
        } else {
            // 기존 관리자 계정이 있으면 환경 변수로 업데이트
            updateExistingAdminPasswords();
        }
        
        if (userRepository.count() == 0) {
            createInitialUser();
        } else {
            // 기존 사용자 계정 비밀번호 업데이트
            updateExistingUserPasswords();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // 1. inquiry.json 이관
        File inquiryFile = new File("data/inquiry.json");
        if (inquiryFile.exists()) {
            Inquiry[] inquiries = mapper.readValue(inquiryFile, Inquiry[].class);
            for (Inquiry inquiry : inquiries) {
                if (!inquiryRepository.existsById(inquiry.getId())) {
                    inquiryRepository.save(inquiry);
                }
            }
        }

        // 2. resumeKR.json, resumeJP.json 이관
        File resumeKRFile = new File("data/resumeKR.json");
        if (resumeKRFile.exists()) {
            Resume resumeKR = mapper.readValue(resumeKRFile, Resume.class);
            resumeKR.setLang("KO");
            // 기존 한국 이력서가 있으면 삭제하고 새로 저장 (강제 업데이트)
            resumeRepository.findByLang("KO").ifPresent(existing -> resumeRepository.deleteById(existing.getId()));
            resumeRepository.save(resumeKR);
            log.info("한국 이력서 데이터를 데이터베이스에 로드했습니다.");
        }
        File resumeJPFile = new File("data/resumeJP.json");
        if (resumeJPFile.exists()) {
            Resume resumeJP = mapper.readValue(resumeJPFile, Resume.class);
            resumeJP.setLang("JA");
            // 기존 일본 이력서가 있으면 삭제하고 새로 저장 (강제 업데이트)
            resumeRepository.findByLang("JA").ifPresent(existing -> resumeRepository.deleteById(existing.getId()));
            resumeRepository.save(resumeJP);
            log.info("일본 이력서 데이터를 데이터베이스에 로드했습니다.");
        }

        // 3. visitor.json은 JSON 파일 기반으로 관리하므로 JPA Entity 사용하지 않음
        
        // 비밀번호 마이그레이션 실행
        migratePasswords();
    }
    

    
    /**
     * 기존 SHA-256 비밀번호에 접두사 추가
     * DelegatingPasswordEncoder가 인식할 수 있도록 {sha256} 접두사 추가
     */
    private void migratePasswords() {
        log.info("비밀번호 마이그레이션 시작...");
        
        // User 테이블의 SHA-256 비밀번호 마이그레이션
        List<User> users = userRepository.findAll();
        int migratedCount = 0;
        
        for (User user : users) {
            String password = user.getPassword();
            
            // SHA-256 해시인데 접두사가 없는 경우 (64자리 16진수)
            if (password.length() == 64 && password.matches("[a-fA-F0-9]+") && 
                !password.startsWith("{") && !password.startsWith("$2a$")) {
                
                // {sha256} 접두사 추가
                user.updatePassword("{sha256}" + password);
                userRepository.save(user);
                migratedCount++;
                log.info("사용자 '{}'의 비밀번호에 {sha256} 접두사 추가", user.getUsername());
            }
        }
        
        // Admin 테이블의 SHA-256 비밀번호 마이그레이션
        List<Admin> admins = adminRepository.findAll();
        int adminMigratedCount = 0;
        
        for (Admin admin : admins) {
            String password = admin.getPassword();
            
            // SHA-256 해시인데 접두사가 없는 경우 (64자리 16진수)
            if (password.length() == 64 && password.matches("[a-fA-F0-9]+") && 
                !password.startsWith("{") && !password.startsWith("$2a$")) {
                
                // {sha256} 접두사 추가
                admin.setPassword("{sha256}" + password);
                adminRepository.save(admin);
                adminMigratedCount++;
                log.info("관리자 '{}'의 비밀번호에 {sha256} 접두사 추가", admin.getUsername());
            }
        }
        
        log.info("비밀번호 마이그레이션 완료: 사용자 {}명, 관리자 {}명", migratedCount, adminMigratedCount);
    }
    
    private void createInitialAdmin() {
        // 환경 변수에서 관리자 정보를 가져옴
        String adminUsername = System.getenv("ADMIN_USERNAME");
        String adminPassword = System.getenv("ADMIN_PASSWORD");
        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminName = System.getenv("ADMIN_NAME");
        
        // 환경 변수가 설정되지 않은 경우 기본값 사용
        if (adminUsername == null) {
            adminUsername = "admin";
        }
        if (adminPassword == null) {
            adminPassword = "admin123";
        }
        if (adminEmail == null) {
            adminEmail = "admin@example.com";
        }
        if (adminName == null) {
            adminName = "Administrator";
        }
        
        Admin admin = new Admin();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
        
        System.out.println("관리자 계정이 생성되었습니다.");
        System.out.println("아이디: " + adminUsername);
        System.out.println("이름: " + adminName);
        System.out.println("비밀번호: " + adminPassword);
        System.out.println("환경 변수를 설정하여 다른 정보를 사용할 수 있습니다.");
    }

    private void createInitialUser() {
        // 테스트용 사용자 계정 생성은 비활성화
        // 실제 운영 환경에서는 사용자가 직접 회원가입을 통해 계정을 생성해야 함
        System.out.println("테스트용 사용자 계정 생성을 건너뜁니다.");
    }
    
    private void createJapaneseAdmin() {
        // 일본어 관리자 계정 생성
        String jpAdminUsername = System.getenv("JP_ADMIN_USERNAME");
        String jpAdminPassword = System.getenv("JP_ADMIN_PASSWORD");
        String jpAdminEmail = System.getenv("JP_ADMIN_EMAIL");
        String jpAdminName = System.getenv("JP_ADMIN_NAME");
        
        // 환경 변수가 설정되지 않은 경우 기본값 사용
        if (jpAdminUsername == null) {
            jpAdminUsername = "admin_jp";
        }
        if (jpAdminPassword == null) {
            jpAdminPassword = "admin123";
        }
        if (jpAdminEmail == null) {
            jpAdminEmail = "admin_jp@example.com";
        }
        if (jpAdminName == null) {
            jpAdminName = "Japanese Administrator";
        }
        
        Admin jpAdmin = new Admin();
        jpAdmin.setUsername(jpAdminUsername);
        jpAdmin.setPassword(passwordEncoder.encode(jpAdminPassword));
        jpAdmin.setName(jpAdminName);
        jpAdmin.setEmail(jpAdminEmail);
        jpAdmin.setActive(true);
        jpAdmin.setCreatedAt(LocalDateTime.now());
        jpAdmin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(jpAdmin);
        
        System.out.println("일본어 관리자 계정이 생성되었습니다.");
        System.out.println("아이디: " + jpAdminUsername);
        System.out.println("이름: " + jpAdminName);
        System.out.println("비밀번호: " + jpAdminPassword);
    }

    private void updateExistingAdminPasswords() {
        System.out.println("기존 관리자 계정 확인 중...");
        
        // 기존 admin 계정들 확인
        long adminCount = adminRepository.count();
        System.out.println("총 관리자 계정 수: " + adminCount);
        
        // 환경 변수에서 관리자 정보 가져오기
        String envAdminUsername = System.getenv("ADMIN_USERNAME");
        String envAdminPassword = System.getenv("ADMIN_PASSWORD");
        String envAdminEmail = System.getenv("ADMIN_EMAIL");
        String envAdminName = System.getenv("ADMIN_NAME");
        
        String envJpAdminUsername = System.getenv("JP_ADMIN_USERNAME");
        String envJpAdminPassword = System.getenv("JP_ADMIN_PASSWORD");
        String envJpAdminEmail = System.getenv("JP_ADMIN_EMAIL");
        String envJpAdminName = System.getenv("JP_ADMIN_NAME");
        

        
        // 기존 관리자 계정들을 환경 변수로 업데이트
        adminRepository.findAll().forEach(admin -> {
            System.out.println("기존 관리자 계정: " + admin.getUsername() + " (ID: " + admin.getId() + ")");
            
            // 한국어 관리자 계정 업데이트
            if (envAdminUsername != null && envAdminPassword != null && 
                (admin.getUsername().equals("admin") || admin.getUsername().equals("원하는아이디"))) {
                
                admin.setUsername(envAdminUsername);
                admin.setPassword(passwordEncoder.encode(envAdminPassword));
                if (envAdminEmail != null) {
                    admin.setEmail(envAdminEmail);
                }
                if (envAdminName != null) {
                    admin.setName(envAdminName);
                }
                adminRepository.save(admin);
                System.out.println("한국어 관리자 계정이 환경 변수로 업데이트되었습니다.");
                System.out.println("새 아이디: " + envAdminUsername);
                System.out.println("새 이름: " + (envAdminName != null ? envAdminName : "기존 이름 유지"));
                System.out.println("새 비밀번호: " + envAdminPassword);
            }
            // 일본어 관리자 계정 업데이트
            else if (envJpAdminUsername != null && envJpAdminPassword != null && 
                     (admin.getUsername().equals("admin_jp") || admin.getName().contains("Japanese"))) {
                
                admin.setUsername(envJpAdminUsername);
                admin.setPassword(passwordEncoder.encode(envJpAdminPassword));
                if (envJpAdminEmail != null) {
                    admin.setEmail(envJpAdminEmail);
                }
                if (envJpAdminName != null) {
                    admin.setName(envJpAdminName);
                }
                adminRepository.save(admin);
                System.out.println("일본어 관리자 계정이 환경 변수로 업데이트되었습니다.");
                System.out.println("새 아이디: " + envJpAdminUsername);
                System.out.println("새 이름: " + (envJpAdminName != null ? envJpAdminName : "기존 이름 유지"));
                System.out.println("새 비밀번호: " + envJpAdminPassword);
            }
            // 환경 변수가 설정되지 않은 경우 기존 계정 유지
            else {
                System.out.println(admin.getUsername() + " 계정은 기존 정보를 유지합니다.");
                System.out.println("환경 변수를 설정하여 계정 정보를 변경할 수 있습니다.");
            }
        });
        
        // 환경 변수가 설정되었지만 해당 계정이 없는 경우 새로 생성
        if (envAdminUsername != null && envAdminPassword != null && 
            adminRepository.findByUsername(envAdminUsername).isEmpty()) {
            createInitialAdmin();
        }
        
        if (envJpAdminUsername != null && envJpAdminPassword != null && 
            adminRepository.findByUsername(envJpAdminUsername).isEmpty()) {
            createJapaneseAdmin();
        }
    }
    
    private void updateExistingUserPasswords() {
        // 테스트용 사용자 계정 비밀번호 업데이트는 비활성화
        System.out.println("테스트용 사용자 계정 비밀번호 업데이트를 건너뜁니다.");
    }

    private void createInitialPosts() {
        
    }
} 