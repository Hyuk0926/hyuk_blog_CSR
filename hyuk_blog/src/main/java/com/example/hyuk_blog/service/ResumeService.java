package com.example.hyuk_blog.service;

import com.example.hyuk_blog.entity.Resume;
import com.example.hyuk_blog.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;

    public enum ResumeLang { KO, JA }

    /**
     * 언어별 이력서 저장
     * @param resume 저장할 이력서 객체
     * @param lang 언어 (ko 또는 ja)
     */
    public void saveResume(Resume resume, String lang) {
        // 언어 설정
        String langUpper = lang.toUpperCase();
        resume.setLang(langUpper);
        
        // 기존 이력서가 있으면 삭제
        resumeRepository.findByLang(langUpper).ifPresent(r -> resumeRepository.deleteById(r.getId()));
        
        // 새 이력서 저장
        resumeRepository.save(resume);
    }

    /**
     * 언어별 이력서 조회
     * @param lang 언어 (ko 또는 ja)
     * @return 해당 언어의 이력서 또는 빈 이력서 객체
     */
    public Resume loadResume(String lang) {
        String langUpper = lang.toUpperCase();
        return resumeRepository.findByLang(langUpper).orElseGet(Resume::new);
    }

    /**
     * 기본 한국어 이력서 저장 (기존 호환성 유지)
     * @param resume 저장할 이력서 객체
     */
    public void saveResume(Resume resume) {
        saveResume(resume, "ko");
    }

    /**
     * 기본 한국어 이력서 조회 (기존 호환성 유지)
     * @return 한국어 이력서 또는 빈 이력서 객체
     */
    public Resume loadResume() {
        return loadResume("ko");
    }
}