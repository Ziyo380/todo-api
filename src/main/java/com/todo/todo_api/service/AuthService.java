package com.todo.todo_api.service;

import com.todo.todo_api.dto.LoginRequest;
import com.todo.todo_api.dto.LoginResponse;
import com.todo.todo_api.dto.RegisterRequest;
import com.todo.todo_api.entity.User;
import com.todo.todo_api.exception.InvalidCredentialsException;
import com.todo.todo_api.exception.ResourceAlreadyExistsException;
import com.todo.todo_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new ResourceAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        User user=User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request){
       try {
           UsernamePasswordAuthenticationToken token =
                   new UsernamePasswordAuthenticationToken(
                           request.getUsername(),
                           request.getPassword());
           authenticationManager.authenticate(token);
           String jwt=jwtService.generateToken(request.getUsername());
           return new LoginResponse(jwt, "Bearer");

       }catch (AuthenticationException e){
           throw new InvalidCredentialsException("Invalid username or password");
       }
    }
}
