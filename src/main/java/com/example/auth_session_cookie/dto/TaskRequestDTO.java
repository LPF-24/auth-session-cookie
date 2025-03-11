package com.example.auth_session_cookie.dto;

import com.example.auth_session_cookie.entity.Employee;
import com.example.auth_session_cookie.util.ValidDeadline;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequestDTO {
    @NotEmpty(message = "Subject can't be empty!")
    @Size(max = 100, message = "The length of the subject must not exceed 100 characters!")
    private String subject;

    private String body;

    @ValidDeadline
    private LocalDateTime deadline;

    private Employee employee;

    private Integer employeeId;
}
