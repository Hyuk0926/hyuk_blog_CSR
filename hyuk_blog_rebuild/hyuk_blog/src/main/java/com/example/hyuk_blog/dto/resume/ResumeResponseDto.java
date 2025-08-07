package com.example.hyuk_blog.dto.resume;

import com.example.hyuk_blog.entity.Resume;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Getter
public class ResumeResponseDto {

    private final String name;
    private final String email;
    private final String phone;
    private final String photoUrl;
    private final String birth;
    private final String address;
    private final List<EducationDto> educations;
    private final String skills;

    // 언어에 따라 선택된 텍스트 필드들
    private final String introduction;
    private final String studentLife;
    private final String strengthsWeaknesses;
    private final String effortExperience;
    private final String japanItMotivation;
    private final String futurePlan;

    // Entity를 DTO로 변환하는 생성자 (언어 선택 로직 포함)
    public ResumeResponseDto(Resume resume, String lang) {
        // 언어에 따라 이름 선택 (null 체크 추가)
        this.name = "ja".equalsIgnoreCase(lang) && resume.getNameJa() != null ? 
                   resume.getNameJa() : 
                   (resume.getNameKo() != null ? resume.getNameKo() : "");
        
        this.email = resume.getEmail() != null ? resume.getEmail() : "";
        this.phone = resume.getPhone() != null ? resume.getPhone() : "";
        this.photoUrl = resume.getPhotoUrl() != null ? resume.getPhotoUrl() : "";
        this.birth = resume.getBirth() != null ? resume.getBirth() : "";
        
        // 언어에 따라 주소 선택 (null 체크 추가)
        this.address = "ja".equalsIgnoreCase(lang) && resume.getAddressJa() != null ? 
                      resume.getAddressJa() : 
                      (resume.getAddressKo() != null ? resume.getAddressKo() : "");
        
        this.skills = resume.getSkills() != null ? resume.getSkills() : "";
        
        // educations null 체크 추가
        if (resume.getEducations() != null) {
            this.educations = resume.getEducations().stream()
                    .map(education -> new EducationDto(education, lang))
                    .collect(Collectors.toList());
        } else {
            this.educations = new ArrayList<>();
        }

        // "ja"가 아니면 기본적으로 한국어(ko)를 반환 (null 체크 추가)
        if ("ja".equalsIgnoreCase(lang)) {
            this.introduction = resume.getIntroductionJa() != null ? resume.getIntroductionJa() : "";
            this.studentLife = resume.getStudentLifeJa() != null ? resume.getStudentLifeJa() : "";
            this.strengthsWeaknesses = resume.getStrengthsWeaknessesJa() != null ? resume.getStrengthsWeaknessesJa() : "";
            this.effortExperience = resume.getEffortExperienceJa() != null ? resume.getEffortExperienceJa() : "";
            this.japanItMotivation = resume.getJapanItMotivationJa() != null ? resume.getJapanItMotivationJa() : "";
            this.futurePlan = resume.getFuturePlanJa() != null ? resume.getFuturePlanJa() : "";
        } else {
            this.introduction = resume.getIntroductionKo() != null ? resume.getIntroductionKo() : "";
            this.studentLife = resume.getStudentLifeKo() != null ? resume.getStudentLifeKo() : "";
            this.strengthsWeaknesses = resume.getStrengthsWeaknessesKo() != null ? resume.getStrengthsWeaknessesKo() : "";
            this.effortExperience = resume.getEffortExperienceKo() != null ? resume.getEffortExperienceKo() : "";
            this.japanItMotivation = resume.getJapanItMotivationKo() != null ? resume.getJapanItMotivationKo() : "";
            this.futurePlan = resume.getFuturePlanKo() != null ? resume.getFuturePlanKo() : "";
        }
    }

    // 학력 정보를 담는 내부 DTO
    @Getter
    public static class EducationDto {
        private final String school;
        private final String degree;
        private final String period;

        public EducationDto(Resume.Education education, String lang) {
            // null 체크 추가
            this.school = "ja".equalsIgnoreCase(lang) && education.getSchoolJa() != null ? 
                         education.getSchoolJa() : 
                         (education.getSchoolKo() != null ? education.getSchoolKo() : "");
            this.degree = "ja".equalsIgnoreCase(lang) && education.getDegreeJa() != null ? 
                         education.getDegreeJa() : 
                         (education.getDegreeKo() != null ? education.getDegreeKo() : "");
            this.period = education.getPeriod() != null ? education.getPeriod() : "";
        }
    }
} 