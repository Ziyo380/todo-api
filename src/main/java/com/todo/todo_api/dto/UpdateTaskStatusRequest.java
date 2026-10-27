package com.todo.todo_api.dto;

import com.todo.todo_api.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    @NotNull(message = "Task status is required")
    private TaskStatus status;
}
