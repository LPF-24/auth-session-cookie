package com.example.auth_session_cookie.repository;

import com.example.auth_session_cookie.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    boolean existsBySubjectAndEmployeeId(String subject, Integer employeeId);
    List<Task> findBySubjectStartingWith(String query);
}
