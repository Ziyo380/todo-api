package com.todo.todo_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username should not be empty")
    @Size(min = 5, max = 25, message = "Username must contain between 5 and 25 characters")
    private String username;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email should not be empty")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    private String password;
}
