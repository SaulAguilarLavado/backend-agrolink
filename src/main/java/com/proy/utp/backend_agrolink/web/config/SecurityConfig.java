package com.proy.utp.backend_agrolink.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.proy.utp.backend_agrolink.web.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // <-- Inyectar el filtro

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de autenticación
                        .requestMatchers("/auth/**").permitAll()

                        // --- 2. AÑADIR NUEVAS REGLAS PARA PRODUCTOS ---
                        .requestMatchers(HttpMethod.GET, "/productos/**").hasAnyAuthority("agricultor", "comprador","administrador")
                        .requestMatchers(HttpMethod.POST, "/productos").hasAuthority("agricultor")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasAuthority("agricultor")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAnyAuthority("agricultor", "administrador")

                        // Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}