package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Role;
import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.repository.RoleRepository;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import com.proy.utp.backend_agrolink.persistance.crud.RolCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RolCrudRepository rolCrudRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@agrolink.com}")
    private String adminEmail;

    @Value("${admin.password:Admin123!}")
    private String adminPassword;

    @Value("${admin.name:Administrador}")
    private String adminName;

    @Value("${admin.lastname:Sistema}")
    private String adminLastname;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           RolCrudRepository rolCrudRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.rolCrudRepository = rolCrudRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("🔄 Iniciando configuración de datos iniciales...");

        // 1. Crear roles si no existen
        createRolesIfNotExist();

        // 2. Crear administrador si no existe
        createAdminIfNotExist();

        log.info("✅ Configuración de datos iniciales completada");
    }

    private void createRolesIfNotExist() {
        String[] roleNames = {"ADMINISTRADOR", "AGRICULTOR", "COMPRADOR"};

        for (String roleName : roleNames) {
            if (rolCrudRepository.findByNombre(roleName).isEmpty()) {
                Rol rol = new Rol();
                rol.setNombre(roleName);
                rolCrudRepository.save(rol);
                log.info("✅ Rol creado: {}", roleName);
            } else {
                log.info("ℹ️  Rol ya existe: {}", roleName);
            }
        }
    }

    private void createAdminIfNotExist() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("ℹ️  El usuario administrador ya existe: {}", adminEmail);
            return;
        }

        Role adminRole = roleRepository.findByRoleName("ADMINISTRADOR")
                .orElseThrow(() -> new RuntimeException(
                        "⚠️ ERROR CRÍTICO: No se pudo encontrar el rol ADMINISTRADOR después de crearlo"
                ));

        User admin = new User();
        admin.setName(adminName);
        admin.setLastname(adminLastname);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setAddress("Sistema Central");
        admin.setPhone("999999999");

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);

        userRepository.save(admin);

        log.info("🎉 ============================================");
        log.info("   Usuario ADMINISTRADOR creado exitosamente");
        log.info("🎉 ============================================");
        log.info("   📧 Email: {}", adminEmail);
        log.info("   🔑 Password: {}", adminPassword);
        log.info("   👤 Nombre: {} {}", adminName, adminLastname);
        log.info("⚠️  IMPORTANTE: Cambia la contraseña después del primer login");
        log.info("================================================");
    }
}