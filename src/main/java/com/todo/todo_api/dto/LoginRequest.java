package com.todo.todo_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username should not be empty")
    private String username;
    @NotBlank(message = "Password should not be empty")
    private String password;
}
