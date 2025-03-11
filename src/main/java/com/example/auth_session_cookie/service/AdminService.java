package com.example.auth_session_cookie.service;

import com.example.auth_session_cookie.dto.EmployeeRequestDTO;
import com.example.auth_session_cookie.dto.TaskRequestDTO;
import com.example.auth_session_cookie.dto.TaskResponseDTO;
import com.example.auth_session_cookie.entity.Employee;
import com.example.auth_session_cookie.entity.Task;
import com.example.auth_session_cookie.repository.EmployeeRepository;
import com.example.auth_session_cookie.repository.TaskRepository;
import com.example.auth_session_cookie.util.EmployeeConverter;
import com.example.auth_session_cookie.util.TaskConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminService {
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final EmployeeConverter employeeConverter;
    private final TaskConverter taskConverter;

    @Autowired
    public AdminService(EmployeeRepository employeeRepository, TaskRepository taskRepository, EmployeeConverter employeeConverter, TaskConverter taskConverter) {
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.employeeConverter = employeeConverter;
        this.taskConverter = taskConverter;
    }

    @Transactional
    public void register(EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = employeeConverter.toEmployee(employeeRequestDTO);
        employeeRepository.saveAndFlush(employee);
    }

    @Transactional
    public void deleteEmployee(int employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @Transactional
    public void createTask(TaskRequestDTO taskDTO) {
        Task task = new Task();
        task.setSubject(taskDTO.getSubject());
        task.setBody(taskDTO.getBody());
        task.setDeadline(taskDTO.getDeadline());

        if (taskDTO.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(taskDTO.getEmployeeId())
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
            task.setEmployee(employee);
        }

        taskRepository.save(task);
    }

    @Transactional
    public void assignTask(int taskId, Integer employeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + employeeId + " not found"));

        task.setEmployee(employee);
        taskRepository.save(task);
    }

    @Transactional
    public void releaseTask(int taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));

        task.setEmployee(null);
    }

    @Transactional
    public void updateTask(int taskId, TaskRequestDTO dto) {
        Task taskToUpdate = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));

        if (dto.getSubject() != null) {
            taskToUpdate.setSubject(dto.getSubject());
        }
        if (dto.getBody() != null) {
            taskToUpdate.setBody(dto.getBody());
        }
        if (dto.getDeadline() != null) {
            taskToUpdate.setDeadline(dto.getDeadline());
        }

        taskRepository.save(taskToUpdate);
    }

    @Transactional
    public void deleteTask(int taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public List<TaskResponseDTO> searchTasksBySubject(String query) {
        return taskRepository.findBySubjectStartingWith(query).stream().map(taskConverter::mapToResponseDTO).toList();
    }
}
