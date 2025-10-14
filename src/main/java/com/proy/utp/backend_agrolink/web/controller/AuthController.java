package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.dto.AuthResponse;
import com.proy.utp.backend_agrolink.domain.dto.LoginRequest;
import com.proy.utp.backend_agrolink.domain.dto.RegisterRequest;
import com.proy.utp.backend_agrolink.domain.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return new ResponseEntity<>("Usuario registrado exitosamente!", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest);
            AuthResponse authResponse = new AuthResponse(token);

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return new ResponseEntity("Email o contraseña inválidos", HttpStatus.UNAUTHORIZED);
        }
    }
}
