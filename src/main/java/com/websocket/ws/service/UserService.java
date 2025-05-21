package com.websocket.ws.service;

import com.websocket.ws.domain.User;

public interface UserService {
    User register(String username, String password);
    User authenticate(String username, String password);
    String generateToken(User user);
} 