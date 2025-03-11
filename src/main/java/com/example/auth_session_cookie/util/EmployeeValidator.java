package com.example.auth_session_cookie.util;

import com.example.auth_session_cookie.dto.EmployeeRequestDTO;
import com.example.auth_session_cookie.dto.EmployeeUpdateDTO;
import com.example.auth_session_cookie.entity.Employee;
import com.example.auth_session_cookie.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class EmployeeValidator implements Validator {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeValidator(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return EmployeeRequestDTO.class.equals(clazz) || EmployeeUpdateDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof EmployeeRequestDTO) {
            validateEmail(((EmployeeRequestDTO) target).getEmail(), null, errors);
        } else if (target instanceof EmployeeUpdateDTO dto) {
            validateEmail(dto.getEmail(), dto.getId(), errors);
        }
    }

    private void validateEmail(String email, Integer id, Errors errors) {
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(email);

        if (existingEmployee.isPresent() && (id == null || !existingEmployee.get().getId().equals(id))) {
            errors.rejectValue("email", "email.exists", "This email is already taken!");
        }
    }
}
