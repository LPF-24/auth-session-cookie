package com.example.auth_session_cookie.dto;

import com.example.auth_session_cookie.util.ValidCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CodeRequestDTO {
    @ValidCode
    private String code;
}
