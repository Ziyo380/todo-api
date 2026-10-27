package com.todo.todo_api.controller;

import com.todo.todo_api.dto.AuthResponse;
import com.todo.todo_api.dto.LoginRequest;
import com.todo.todo_api.dto.LoginResponse;
import com.todo.todo_api.dto.RegisterRequest;
import com.todo.todo_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
            ){
        String message=authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(message));
    }
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody @Valid LoginRequest request
            ){

        return authService.login(request);
    }
}
