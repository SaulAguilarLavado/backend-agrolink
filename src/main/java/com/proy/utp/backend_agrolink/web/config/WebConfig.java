package com.proy.utp.backend_agrolink.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a TODOS los endpoints de la API (ej: /auth/**, /productos/**, etc.)
                .allowedOrigins("http://localhost:3000") // El origen de tu frontend de React
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos los encabezados (como 'Authorization')
                .allowCredentials(true); // Permite el envío de cookies o tokens de autenticación
    }
}