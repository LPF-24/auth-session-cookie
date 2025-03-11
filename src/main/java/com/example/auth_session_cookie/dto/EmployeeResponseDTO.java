package com.example.auth_session_cookie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeResponseDTO {
    private Integer id;

    private String employeeName;

    private String position;

    private String email;

    private String address;

    private List<TaskResponseDTO> taskList;
}
