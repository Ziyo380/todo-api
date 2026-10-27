package com.todo.todo_api.repository;

import com.todo.todo_api.entity.Task;
import com.todo.todo_api.entity.TaskPriority;
import com.todo.todo_api.entity.TaskStatus;
import com.todo.todo_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByUser(User user);

    List<Task> findByUserAndStatus(User user, TaskStatus status);

    Optional<Task> findByIdAndUser(Long id, User user);

    @Query("""
            select task from Task task
            where task.user = :user
              and (:status is null or task.status = :status)
              and (:priority is null or task.priority = :priority)
              and (:searchTerm is null
                   or lower(task.title) like lower(concat('%', :searchTerm, '%'))
                   or lower(coalesce(task.description, '')) like lower(concat('%', :searchTerm, '%')))
            """)
    Page<Task> search(
            @Param("user") User user,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}
