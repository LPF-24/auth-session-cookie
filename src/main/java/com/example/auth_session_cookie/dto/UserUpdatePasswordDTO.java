package com.example.auth_session_cookie.dto;

import com.example.auth_session_cookie.util.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdatePasswordDTO {
    private Integer id;

    @NotEmpty(message = "Old password can't be empty")
    private String oldPassword;

    @NotEmpty(message = "New password can't be empty!")
    @ValidPassword
    private String newPassword;
}
