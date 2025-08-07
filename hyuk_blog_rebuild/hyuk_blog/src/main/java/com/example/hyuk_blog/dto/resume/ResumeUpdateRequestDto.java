package com.example.hyuk_blog.dto.resume;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResumeUpdateRequestDto {

    private String nameKo;
    private String nameJa;
    private String email;
    private String phone;
    private String photoUrl;
    private String birth;
    private String addressKo;
    private String addressJa;
    private List<EducationDto> educations;
    private String skills;

    // 한국어 필드
    private String introductionKo;
    private String studentLifeKo;
    private String strengthsWeaknessesKo;
    private String effortExperienceKo;
    private String japanItMotivationKo;
    private String futurePlanKo;

    // 일본어 필드
    private String introductionJa;
    private String studentLifeJa;
    private String strengthsWeaknessesJa;
    private String effortExperienceJa;
    private String japanItMotivationJa;
    private String futurePlanJa;

    // 학력 정보를 담는 내부 DTO
    @Getter
    @Setter
    public static class EducationDto {
        private String schoolKo;
        private String schoolJa;
        private String degreeKo;
        private String degreeJa;
        private String period;
    }
} 