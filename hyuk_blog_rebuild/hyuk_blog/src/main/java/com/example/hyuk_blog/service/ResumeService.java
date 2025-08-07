package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.resume.ResumeResponseDto;
import com.example.hyuk_blog.dto.resume.ResumeUpdateRequestDto;
import com.example.hyuk_blog.entity.Resume;
import com.example.hyuk_blog.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;

    public enum ResumeLang { KO, JA }

    public void saveResume(Resume resume) {
        resume.setLang(ResumeLang.KO.name());
        resumeRepository.findByLang(ResumeLang.KO.name()).ifPresent(r -> resumeRepository.deleteById(r.getId()));
        resumeRepository.save(resume);
    }

    public Resume loadResume() {
        return resumeRepository.findByLang(ResumeLang.KO.name()).orElseGet(Resume::new);
    }

    /**
     * 언어별 이력서 조회
     * @param lang 언어 코드 (ko, ja)
     * @return ResumeResponseDto
     */
    public ResumeResponseDto getResumeByLang(String lang) {
        // 기본값은 한국어
        String defaultLang = "ko";
        if (lang != null && !lang.trim().isEmpty()) {
            defaultLang = lang.toLowerCase();
        }
        
        // 한국어 이력서 조회 (KO로 저장된 데이터)
        Resume resume = resumeRepository.findByLang("KO").orElseGet(() -> {
            Resume newResume = new Resume();
            newResume.setEducations(new ArrayList<>()); // 빈 리스트로 초기화
            newResume.setNameKo("");
            newResume.setNameJa("");
            newResume.setEmail("");
            newResume.setPhone("");
            newResume.setPhotoUrl("");
            newResume.setBirth("");
            newResume.setAddressKo("");
            newResume.setAddressJa("");
            newResume.setSkills("");
            newResume.setIntroductionKo("");
            newResume.setIntroductionJa("");
            newResume.setStudentLifeKo("");
            newResume.setStudentLifeJa("");
            newResume.setStrengthsWeaknessesKo("");
            newResume.setStrengthsWeaknessesJa("");
            newResume.setEffortExperienceKo("");
            newResume.setEffortExperienceJa("");
            newResume.setJapanItMotivationKo("");
            newResume.setJapanItMotivationJa("");
            newResume.setFuturePlanKo("");
            newResume.setFuturePlanJa("");
            return newResume;
        });
        
        // educations가 null인 경우 빈 리스트로 초기화
        if (resume.getEducations() == null) {
            resume.setEducations(new ArrayList<>());
        }
        
        return new ResumeResponseDto(resume, defaultLang);
    }

    /**
     * 이력서 수정
     * @param requestDto 수정할 이력서 정보
     * @return 수정된 이력서 정보 (한국어)
     */
    public ResumeResponseDto updateResume(ResumeUpdateRequestDto requestDto) {
        Resume resume = resumeRepository.findByLang(ResumeLang.KO.name()).orElse(new Resume());
        
        // 기본 정보 업데이트
        resume.setNameKo(requestDto.getNameKo());
        resume.setNameJa(requestDto.getNameJa());
        resume.setEmail(requestDto.getEmail());
        resume.setPhone(requestDto.getPhone());
        resume.setPhotoUrl(requestDto.getPhotoUrl());
        resume.setBirth(requestDto.getBirth());
        resume.setAddressKo(requestDto.getAddressKo());
        resume.setAddressJa(requestDto.getAddressJa());
        resume.setSkills(requestDto.getSkills());
        
        // 한국어 필드 업데이트
        resume.setIntroductionKo(requestDto.getIntroductionKo());
        resume.setStudentLifeKo(requestDto.getStudentLifeKo());
        resume.setStrengthsWeaknessesKo(requestDto.getStrengthsWeaknessesKo());
        resume.setEffortExperienceKo(requestDto.getEffortExperienceKo());
        resume.setJapanItMotivationKo(requestDto.getJapanItMotivationKo());
        resume.setFuturePlanKo(requestDto.getFuturePlanKo());
        
        // 일본어 필드 업데이트
        resume.setIntroductionJa(requestDto.getIntroductionJa());
        resume.setStudentLifeJa(requestDto.getStudentLifeJa());
        resume.setStrengthsWeaknessesJa(requestDto.getStrengthsWeaknessesJa());
        resume.setEffortExperienceJa(requestDto.getEffortExperienceJa());
        resume.setJapanItMotivationJa(requestDto.getJapanItMotivationJa());
        resume.setFuturePlanJa(requestDto.getFuturePlanJa());
        
        // 학력 정보 업데이트
        if (requestDto.getEducations() != null) {
            resume.setEducations(requestDto.getEducations().stream()
                    .map(this::convertToEducationEntity)
                    .toList());
        }
        
        resume.setLang(ResumeLang.KO.name());
        Resume savedResume = resumeRepository.save(resume);
        
        return new ResumeResponseDto(savedResume, "ko");
    }

    /**
     * EducationDto를 Resume.Education 엔티티로 변환
     */
    private Resume.Education convertToEducationEntity(ResumeUpdateRequestDto.EducationDto dto) {
        Resume.Education education = new Resume.Education();
        education.setSchoolKo(dto.getSchoolKo());
        education.setSchoolJa(dto.getSchoolJa());
        education.setDegreeKo(dto.getDegreeKo());
        education.setDegreeJa(dto.getDegreeJa());
        education.setPeriod(dto.getPeriod());
        return education;
    }
}