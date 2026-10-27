package com.todo.todo_api.service;

import com.todo.todo_api.dto.CreateTaskRequest;
import com.todo.todo_api.dto.PageResponse;
import com.todo.todo_api.dto.TaskResponse;
import com.todo.todo_api.dto.UpdateTaskRequest;
import com.todo.todo_api.entity.Task;
import com.todo.todo_api.entity.TaskPriority;
import com.todo.todo_api.entity.TaskStatus;
import com.todo.todo_api.entity.User;
import com.todo.todo_api.exception.ResourceNotFoundException;
import com.todo.todo_api.repository.TaskRepository;
import com.todo.todo_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "dueDate", "title", "status", "priority"
    );

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    public TaskResponse createTask(CreateTaskRequest request){
        User user=getCurrentUser();

        Task task=Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .priority(request.getPriority() == null ? TaskPriority.MEDIUM : request.getPriority())
                .dueDate(request.getDueDate())
                .user(user)
                .build();
        taskRepository.save(task);
        return maptoResponse(task);
    }

    public TaskResponse updateTask(UpdateTaskRequest request, Long id){
        Task task=getOwnedTask(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
        taskRepository.save(task);
        return maptoResponse(task);
    }


    public List<TaskResponse> getAllTasks(){
        User user=getCurrentUser();
        return taskRepository.findByUser(user)
                .stream()
                .map(this::maptoResponse)
                .toList();
    }
    public TaskResponse getTasksById(Long id){
        Task task=getOwnedTask(id);
        return maptoResponse(task);
    }
    public void deleteTask(Long id){
        Task task = getOwnedTask(id);
        taskRepository.delete(task);
    }
    public TaskResponse updateStatus(Long id,TaskStatus status){
        Task task=getOwnedTask(id);
        task.setStatus(status);

        taskRepository.save(task);

        return maptoResponse(task);
    }
    public List<TaskResponse> getTasksByStatus(TaskStatus status){
        User user=getCurrentUser();
        return taskRepository.findByUserAndStatus(user,status)
                .stream()
                .map(this::maptoResponse)
                .toList();
    }

    public PageResponse<TaskResponse> searchTasks(
            TaskStatus status,
            TaskPriority priority,
            String searchTerm,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        User user = getCurrentUser();
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction sortDirection = "asc".equals(direction.toLowerCase(Locale.ROOT))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortDirection, safeSortBy));
        String normalizedSearchTerm = searchTerm == null || searchTerm.isBlank()
                ? null
                : searchTerm.trim();

        Page<TaskResponse> result = taskRepository
                .search(user, status, priority, normalizedSearchTerm, pageable)
                .map(this::maptoResponse);

        return new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    private Task getOwnedTask(Long id){
        User user=getCurrentUser();
        return taskRepository.findByIdAndUser(id,user)
                .orElseThrow(()-> new ResourceNotFoundException("Task not found"));
    }

    private TaskResponse maptoResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .username(task.getUser().getUsername())
                .createdAt(task.getCreatedAt())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .build();
    }
}
