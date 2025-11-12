package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Role;
import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.dto.AuthResponse;
import com.proy.utp.backend_agrolink.domain.dto.LoginRequest;
import com.proy.utp.backend_agrolink.domain.dto.RegisterRequest;
import com.proy.utp.backend_agrolink.domain.repository.RoleRepository;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import com.proy.utp.backend_agrolink.web.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Tu método register(...) no cambia
    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("El email ya está en uso.");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setLastname(registerRequest.getLastname());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAddress(registerRequest.getAddress());
        user.setPhone(registerRequest.getPhone());

        String userType = registerRequest.getUserType().toLowerCase();
        Role userRole = roleRepository.findByRoleName(userType)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + userType));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }


    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName())
                .toList();

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getLastname(),
                roles
        );
    }

}