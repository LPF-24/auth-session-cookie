package com.example.auth_session_cookie.dto;

import com.example.auth_session_cookie.util.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeRequestDTO {
    private Integer id;

    @NotEmpty(message = "Employee name can't be empty!")
    @Size(min = 3, max = 100, message = "Employee name must contain from 4 to 100 characters!")
    private String employeeName;

    @NotEmpty(message = "Password can't be empty!")
    @ValidPassword
    private String password;

    @NotEmpty(message = "Position can't be empty!")
    private String position;

    @NotEmpty(message = "Email can't be empty")
    @Email(message = "Email should be valid!")
    private String email;

    @Pattern(regexp = "[A-Z]\\w+, [A-Z]\\w+, \\d{6}",
            message = "Office address should be in this format: Country, City, Postal Code (6 digits)")
    private String address;

    private String role;
}
