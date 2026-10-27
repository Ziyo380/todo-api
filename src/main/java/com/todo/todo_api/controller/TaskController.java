package com.todo.todo_api.controller;

import com.todo.todo_api.dto.CreateTaskRequest;
import com.todo.todo_api.dto.PageResponse;
import com.todo.todo_api.dto.TaskResponse;
import com.todo.todo_api.dto.UpdateTaskRequest;
import com.todo.todo_api.dto.UpdateTaskStatusRequest;
import com.todo.todo_api.entity.TaskPriority;
import com.todo.todo_api.entity.TaskStatus;
import com.todo.todo_api.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid CreateTaskRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request));
    }
    @GetMapping
    public List<TaskResponse> getAllTasks(){
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTasksById(id);
    }

    @PutMapping("/{id}/status")
    public TaskResponse updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status
            ){
        return taskService.updateStatus(id,status);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse patchStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTaskStatusRequest request
    ) {
        return taskService.updateStatus(id, request.getStatus());
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @RequestBody @Valid UpdateTaskRequest request){
        return taskService.updateTask(request, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/status/{status}")
    public List<TaskResponse> getTasksByStatus(@PathVariable TaskStatus status){
        return taskService.getTasksByStatus(status);
    }

    @GetMapping("/search")
    public PageResponse<TaskResponse> searchTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return taskService.searchTasks(status, priority, query, page, size, sortBy, direction);
    }

}
