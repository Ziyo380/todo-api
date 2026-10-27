package com.todo.todo_api.dto;

import com.todo.todo_api.entity.TaskPriority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest {
    @NotBlank(message = "Task title should not be empty")
    @Size(max = 120, message = "Task title must not exceed 120 characters")
    private String title;
    @Size(max = 1000, message = "Task description must not exceed 1000 characters")
    private String description;

    private TaskPriority priority;

    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDateTime dueDate;
}
