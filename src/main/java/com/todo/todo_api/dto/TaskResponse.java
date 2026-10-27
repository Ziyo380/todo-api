package com.todo.todo_api.dto;

import com.todo.todo_api.entity.TaskPriority;
import com.todo.todo_api.entity.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private String username;

    private LocalDateTime createdAt;

    private TaskPriority priority;

    private LocalDateTime dueDate;
}
