package com.example.auth_session_cookie.service;

import com.example.auth_session_cookie.dto.EmployeeResponseDTO;
import com.example.auth_session_cookie.dto.TaskResponseDTO;
import com.example.auth_session_cookie.entity.Task;
import com.example.auth_session_cookie.repository.TaskRepository;
import com.example.auth_session_cookie.util.EmployeeConverter;
import com.example.auth_session_cookie.util.TaskConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskConverter taskConverter;
    private final EmployeeConverter employeeConverter;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskConverter taskConverter, EmployeeConverter employeeConverter) {
        this.taskRepository = taskRepository;
        this.taskConverter = taskConverter;
        this.employeeConverter = employeeConverter;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(taskConverter::mapToResponseDTO).toList();
    }

    public TaskResponseDTO getTaskById(int taskId) {
        return taskRepository.findById(taskId).map(taskConverter::mapToResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));
    }

    public EmployeeResponseDTO getEmployeeByTaskId(int taskId) {
        return taskRepository.findById(taskId).map(Task::getEmployee)
                .map(employeeConverter::mapToEmployeeResponseDTO).orElse(null);
    }

    public boolean isExpired(LocalDateTime deadLine) {
        if (deadLine == null) {
            return false;
        }

        return deadLine.isBefore(LocalDateTime.now().withSecond(0).withNano(0));
    }
}
