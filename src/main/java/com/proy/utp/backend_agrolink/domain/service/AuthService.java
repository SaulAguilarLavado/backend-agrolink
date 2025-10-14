package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Role;
import com.proy.utp.backend_agrolink.domain.User;
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
import java.util.Set;

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


    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }
}