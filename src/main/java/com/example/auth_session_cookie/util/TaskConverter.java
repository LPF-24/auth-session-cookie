package com.example.auth_session_cookie.util;

import com.example.auth_session_cookie.dto.TaskResponseDTO;
import com.example.auth_session_cookie.entity.Task;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskConverter {
    private final ModelMapper modelMapper;

    public TaskConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TaskResponseDTO mapToResponseDTO(Task task) {
        return modelMapper.map(task, TaskResponseDTO.class);
    }
}
