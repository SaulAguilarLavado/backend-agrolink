package com.proy.utp.backend_agrolink.web.config;

import com.proy.utp.backend_agrolink.web.security.JwtAuthenticationFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // ============================================
    //               AUTH BEANS
    // ============================================
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // ============================================
    //                 CORS CONFIG
    // ============================================
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //  IMPORTANTE: PARA CORS CON CREDENTIALS
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));

        //  Permitir métodos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permitir headers comunes del frontend
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ============================================
    //           SECURITY FILTER CHAIN
    // ============================================
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1️ CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 2️ CSRF deshabilitado
        http.csrf(csrf -> csrf.disable());

        // 3️ Reglas de autorización
        http.authorizeHttpRequests(auth -> auth

                // ===========================
                //   SWAGGER PUBLICO
                // ===========================
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()

                // ===========================
                //   ENDPOINTS PUBLICOS
                // ===========================
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ===========================
                //   PRODUCTOS
                // ===========================
                .requestMatchers(HttpMethod.GET, "/productos/**")
                .hasAnyRole("AGRICULTOR", "COMPRADOR", "ADMINISTRADOR")

                .requestMatchers(HttpMethod.POST, "/productos/**")
                .hasRole("AGRICULTOR")

                .requestMatchers(HttpMethod.PUT, "/productos/**")
                .hasRole("AGRICULTOR")

                .requestMatchers(HttpMethod.DELETE, "/productos/**")
                .hasAnyRole("AGRICULTOR", "ADMINISTRADOR")

                // ===========================
                //   RESTO AUTENTICADO
                // ===========================
                .anyRequest().authenticated()
        );

        // 4️ FILTRO JWT
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
