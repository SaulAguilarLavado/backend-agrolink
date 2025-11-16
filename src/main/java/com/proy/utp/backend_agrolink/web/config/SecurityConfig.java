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

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Bean de CORS para permitir la conexión del frontend
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // O la URL de tu frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Activamos la configuración de CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 2. Desactivamos CSRF
        http.csrf(csrf -> csrf.disable());

        // 3. Definimos las reglas de autorización de forma ordenada
        http.authorizeHttpRequests(auth -> auth
                // --- REGLAS PÚBLICAS (sin autenticación) ---
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Para las peticiones "preflight" de CORS

                // --- REGLAS PARA PRODUCTOS ---
                .requestMatchers(HttpMethod.GET, "/productos", "/productos/**").hasAnyRole("AGRICULTOR", "COMPRADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/productos").hasRole("AGRICULTOR")
                .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("AGRICULTOR")
                .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAnyRole("AGRICULTOR", "ADMINISTRADOR")

                // --- REGLAS PARA CULTIVOS (Ejemplo, ya las tienes en los controladores) ---
                // No es necesario definirlas aquí si usas @PreAuthorize, pero lo mantenemos como ejemplo.
                // .requestMatchers("/cultivos/**").hasRole("AGRICULTOR")

                // --- REGLA FINAL: CUALQUIER OTRA PETICIÓN REQUIERE AUTENTICACIÓN ---
                .anyRequest().authenticated()
        );

        // 4. Añadimos nuestro filtro JWT
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}