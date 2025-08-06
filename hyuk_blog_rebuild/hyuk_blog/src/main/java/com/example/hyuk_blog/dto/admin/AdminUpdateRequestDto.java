package com.example.hyuk_blog.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateRequestDto {

    @Size(min = 4, max = 15, message = "아이디는 4자 이상 15자 이하로 입력해주세요.")
    private String username;

    @Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")
    private String password;

    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private Boolean active;
} 