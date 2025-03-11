package com.example.auth_session_cookie.util;

import com.example.auth_session_cookie.dto.EmployeeRequestDTO;
import com.example.auth_session_cookie.dto.EmployeeResponseDTO;
import com.example.auth_session_cookie.entity.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConverter {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeConverter(ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public EmployeeResponseDTO mapToEmployeeResponseDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeResponseDTO.class);
    }

    public Employee toEmployee(EmployeeRequestDTO dto) {
        Employee employee = modelMapper.map(dto, Employee.class);
        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
        employee.setRole("ROLE_USER");
        return employee;
    }
}
