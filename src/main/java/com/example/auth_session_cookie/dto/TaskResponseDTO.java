package com.example.auth_session_cookie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class TaskResponseDTO {
    private Integer id;

    private String subject;

    private String body;

    private LocalDateTime deadline;

    private Integer employeeId;
}
