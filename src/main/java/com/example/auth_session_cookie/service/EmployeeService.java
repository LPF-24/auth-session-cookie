package com.example.auth_session_cookie.service;

import com.example.auth_session_cookie.dto.EmployeeResponseDTO;
import com.example.auth_session_cookie.dto.EmployeeUpdateDTO;
import com.example.auth_session_cookie.dto.TaskResponseDTO;
import com.example.auth_session_cookie.dto.UserUpdatePasswordDTO;
import com.example.auth_session_cookie.entity.Employee;
import com.example.auth_session_cookie.repository.EmployeeRepository;
import com.example.auth_session_cookie.util.EmployeeConverter;
import com.example.auth_session_cookie.util.PasswordDoesNotMatchException;
import com.example.auth_session_cookie.util.TaskConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final TaskConverter taskConverter;
    private final EmployeeConverter employeeConverter;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, TaskConverter taskConverter, EmployeeConverter employeeConverter, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.taskConverter = taskConverter;
        this.employeeConverter = employeeConverter;
        this.passwordEncoder = passwordEncoder;
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream().map(employeeConverter::mapToEmployeeResponseDTO).toList();
    }

    public EmployeeResponseDTO getEmployeeById(int id) {
        return employeeRepository.findById(id).map(employeeConverter::mapToEmployeeResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + id + " not found"));
    }

    public List<TaskResponseDTO> getTasksById(int id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee with id " + id + " not found"));

        return employee.getTaskList().stream().map(taskConverter::mapToResponseDTO).toList();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    public void updateEmployee(int employeeId, EmployeeUpdateDTO dto) {
        Employee employeeToUpdate = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + employeeId + " not found"));

        BeanUtils.copyProperties(dto, employeeToUpdate, getNullPropertyNames(dto));

        employeeRepository.save(employeeToUpdate);
    }

    @Transactional
    public void updatePassword(int employeeId, UserUpdatePasswordDTO dto) {
        Employee employeeToUpdate = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + employeeId + " not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), employeeToUpdate.getPassword())) {
            throw new PasswordDoesNotMatchException("The passwords do not match");
        }

        employeeToUpdate.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        employeeRepository.save(employeeToUpdate);
    }

    @Transactional
    public void promoteEmployee(Integer employeeId) {
        Employee employeeToPromote = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + employeeId + " not found"));

        employeeToPromote.setRole("ROLE_ADMIN");
        employeeRepository.save(employeeToPromote);
    }

    private String[] getNullPropertyNames(Object source) {
        try {
            return Arrays.stream(Introspector.getBeanInfo(source.getClass(), Object.class)
                            .getPropertyDescriptors())
                    .map(PropertyDescriptor::getName)
                    .filter(name -> {
                        try {
                            return Objects.isNull(new PropertyDescriptor(name, source.getClass()).getReadMethod().invoke(source));
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toArray(String[]::new);
        } catch (IntrospectionException e) {
            throw new RuntimeException("Error processing object properties", e);
        }
    }
}
