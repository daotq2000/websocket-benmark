package com.websocket.ws.controller;

import com.websocket.ws.domain.User;
import com.websocket.ws.dto.LoginRequest;
import com.websocket.ws.dto.RegisterRequest;
import com.websocket.ws.dto.AuthResponse;
import com.websocket.ws.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword());
        String token = userService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getUsername(), request.getPassword());
        String token = userService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }
} 